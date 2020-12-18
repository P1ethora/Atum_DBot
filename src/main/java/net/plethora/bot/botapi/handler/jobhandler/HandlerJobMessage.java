package net.plethora.bot.botapi.handler.jobhandler;

import jdk.nashorn.internal.runtime.Context;
import net.plethora.bot.botapi.handler.jobhandler.parsers.VacancyFormatBuilder;
import net.plethora.bot.botapi.keyboards.kbjob.KeyboardPeriodJob;
import net.plethora.bot.botapi.state.SubState;
import net.plethora.bot.botapi.system.ShiftViewVacancy;
import net.plethora.bot.botapi.system.systemMessage.SearchDataJobMessage;
import net.plethora.bot.cache.CacheVacancySearchUser;
import net.plethora.bot.cache.ProvisionalObjectInfo;
import net.plethora.bot.dao.DataAccessUser;
import net.plethora.bot.model.User;
import net.plethora.bot.model.Vacancy;
import net.plethora.bot.model.systemmodel.InfoForSearch;
import net.plethora.bot.botapi.handler.jobhandler.parsers.ParsRabota;
import net.plethora.bot.dao.DataAccessArea;
import net.plethora.bot.model.systemmodel.SaveVacancyCell;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.ArrayList;
import java.util.List;

@Component
public class HandlerJobMessage<T> {

    private DataAccessArea dataAccessArea;
    private ParsRabota parsRabota;
    private KeyboardPeriodJob keyboardPeriodJob;
    private DataAccessUser dataAccessUser;
    private SearchDataJobMessage searchDataJobMessage;
    private ShiftViewVacancy shiftViewVacancy;
    private ProvisionalObjectInfo provisionalObjectInfo;
    private CacheVacancySearchUser cacheVacancySearchUser;

    public HandlerJobMessage(DataAccessArea dataAccessArea, ParsRabota parsRabota, KeyboardPeriodJob keyboardPeriodJob,
                             DataAccessUser dataAccessUser, SearchDataJobMessage searchDataJobMessage,
                             ShiftViewVacancy shiftViewVacancy, ProvisionalObjectInfo provisionalObjectInfo,
                             CacheVacancySearchUser cacheVacancySearchUser) {
        this.dataAccessArea = dataAccessArea;
        this.parsRabota = parsRabota;
        this.keyboardPeriodJob = keyboardPeriodJob;
        this.dataAccessUser = dataAccessUser;
        this.searchDataJobMessage = searchDataJobMessage;
        this.shiftViewVacancy = shiftViewVacancy;
        this.provisionalObjectInfo = provisionalObjectInfo;
        this.cacheVacancySearchUser = cacheVacancySearchUser;

    }

    public List<T> handler(long chatId, String msgUser, User user, int messageId) {

        List<T> messages = new ArrayList<>();

        if (msgUser.length() > 8 && msgUser.substring(0, 8).equals(":ar!-ea>")) {

            InfoForSearch info = createInfo(messageId,
                    getPartTheInfoUser(msgUser, 0),
                    getPartTheInfoUser(msgUser, 1));

            provisionalObjectInfo.getInfoForSearches().put(chatId, info);
            dataAccessUser.editUser(user, SubState.AREA);//изменяем подсостояние
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(chatId);
            editMessageText.setMessageId(messageId);
            editMessageText.setText("Введите город:");
            messages.add((T) editMessageText);
        } else if (msgUser.length() > 8 && msgUser.substring(0, 8).equals(":period>")) {

            InfoForSearch info = createInfo(messageId,
                    getPartTheInfoUser(msgUser, 0),
                    getPartTheInfoUser(msgUser, 1));

            provisionalObjectInfo.getInfoForSearches().put(chatId, info);
            dataAccessUser.editUser(user, SubState.PERIOD);
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(chatId);
            editMessageText.setMessageId(messageId);
            editMessageText.setText("Выберите период:");
            editMessageText.setReplyMarkup(keyboardPeriodJob.addKeyBoard());
            messages.add((T) editMessageText);

        } else if (msgUser.length() > 8 && msgUser.substring(0, 8).equals(":search>")) {

            InfoForSearch info = createInfo(messageId,
                    getPartTheInfoUser(msgUser, 0),
                    getPartTheInfoUser(msgUser, 1));
            //TODO изменить на editText
            if (info.getArea().equals("null") && info.getPeriod().equals("null")) {             //проверки на нулл значения
                messages.add((T) new SendMessage(chatId, "поле город и период не заполнено"));
            } else if (info.getArea().equals("null")) {
                messages.add((T) new SendMessage(chatId, "поле город не заполнено"));
            } else if (info.getPeriod().equals("null")) {
                messages.add((T) new SendMessage(chatId, "поле период не заполнено"));
            } else {
                int code = dataAccessArea.handleRequest(info.getArea()).getCode();
                List<Vacancy> vacancies = parsRabota.parse(code, info.getPeriod()); //Получаем списо вакансий
                if (vacancies.size() > 0) {
                    SaveVacancyCell saveVacancyCell = createSaveVacancyCell(messageId, chatId, info.getArea(), info.getPeriod(), vacancies); //создать конструктор
                    addSaveVacancyToCache(saveVacancyCell);  //добавляем сохранение
                    messages = shiftViewVacancy.view(chatId, messageId, info, false, false);  //сообщение
                    dataAccessUser.editUser(user, SubState.FOUND);
                } else {
                    messages.add((T) new SendMessage(chatId, "Не обнаружено"));
                }
            }
        } else {

            if (user.getSubState() == SubState.AREA) {
                InfoForSearch info = provisionalObjectInfo.getInfoForSearches().get(chatId);
                if (info == null) { //на случай падения или перезапуска сервера, когда очистится кэш;
                    messages.add((T) searchDataJobMessage.message(chatId, new InfoForSearch()));
                } else {
                    int msgIdPost = info.getId(); //вытягиваем id поста бота
                    if (dataAccessArea.handleRequest(msgUser) == null) {
                        EditMessageText editMessageText = new EditMessageText();
                        editMessageText.setChatId(chatId);
                        editMessageText.setMessageId(msgIdPost);
                        editMessageText.setText("Город не найден, попробуйте ввести заново");

                        messages.add((T) editMessageText); //TODO добавить: вернуться к запросу
                    } else {
                        info.setArea(msgUser);
                        messages.add((T) searchDataJobMessage.editMessage(chatId, msgIdPost, info));
                    }
                }
            } else if (user.getSubState() == SubState.PERIOD) {

                if (msgUser.equals("месяц") ||
                        msgUser.equals("неделя") ||
                        msgUser.equals("три дня") ||
                        msgUser.equals("сутки")) {

                    InfoForSearch info = provisionalObjectInfo.getInfoForSearches().get(chatId);
                    info.setPeriod(msgUser);
                    messages.add((T) searchDataJobMessage.editMessage(chatId, messageId, info));
                } else {
                    InfoForSearch info = provisionalObjectInfo.getInfoForSearches().get(chatId);
                    EditMessageText editMessageText = new EditMessageText();
                    editMessageText.setMessageId(info.getId());
                    editMessageText.setChatId(chatId);
                    editMessageText.setText("Неверно указан период");
                    editMessageText.setReplyMarkup(keyboardPeriodJob.addKeyBoard());
                    messages.add((T) editMessageText);
                }
            } else if (user.getSubState() == SubState.FOUND) {
                if (msgUser.length() > 8 && msgUser.substring(0, 8).equals(":return>")) {
                    String area = getPartTheInfoUser(msgUser, 0);
                    String period = getPartTheInfoUser(msgUser, 1);
                    String idInfo = getPartTheInfoUser(msgUser, 2);
                    InfoForSearch info = createInfo(messageId, area, period);
                    messages.add((T) searchDataJobMessage.editMessage(chatId, messageId, info));
                } else if (msgUser.length() > 8 && msgUser.substring(0, 8).equals(":next-->")) {//TODO записать в колл бек id search job
                    String area = getPartTheInfoUser(msgUser, 0);
                    String period = getPartTheInfoUser(msgUser, 1);
                    int idVacancy = Integer.parseInt(getPartTheInfoUser(msgUser, 2));
                    InfoForSearch info = new InfoForSearch(messageId, chatId, area, period, idVacancy);
                    messages = shiftViewVacancy.view(chatId, messageId, info, true, false);
                } else if (msgUser.length() > 8 && msgUser.substring(0, 8).equals(":<--back")) {
                    String area = getPartTheInfoUser(msgUser, 0);
                    String period = getPartTheInfoUser(msgUser, 1);
                    int idVacancy = Integer.parseInt(getPartTheInfoUser(msgUser, 2));
                    InfoForSearch info = new InfoForSearch(messageId, chatId, area, period, idVacancy);
                    messages = shiftViewVacancy.view(chatId, messageId, info, false, true);
                } else {
                    messages.add((T) new SendMessage(chatId, "Неверный формат ввода"));
                }
            }

        }
        return messages;
    }

    private String getPartTheInfoUser(String msgUser, int numberArr) {
        String data = msgUser.substring(8);
        String[] infos = data.split("\\$");
        return infos[numberArr];
    }

    private SaveVacancyCell createSaveVacancyCell(int messageId, long chatId, String area, String period, List<Vacancy> listVacancy) {
        SaveVacancyCell saveVacancyCell = new SaveVacancyCell();
        saveVacancyCell.setId(messageId);
        saveVacancyCell.setChatId(chatId);
        saveVacancyCell.setArea(area);
        saveVacancyCell.setPeriod(period);
        saveVacancyCell.setVacancies(listVacancy.toArray(new Vacancy[0]));
        return saveVacancyCell;
    }

    private void addSaveVacancyToCache(SaveVacancyCell saveVacancyCell) {
        if (saveVacancyCell == null) {
            new Context.ThrowErrorManager().error("SaveVacancyCell is null, cause \"createSaveVacancyCell(...)\"");
        } else
            cacheVacancySearchUser.getCache().add(saveVacancyCell);
    }

    private InfoForSearch createInfo(int messageId, String area, String period) {
        InfoForSearch info = new InfoForSearch();
        if (area.equals("null"))
            area = null;
        if (period.equals("null"))
            period = null;

        info.setId(messageId);
        info.setArea(area);
        info.setPeriod(period);
        info.setIdVacancy(0);
        return info;
    }

}