package net.plethora.bot.botapi.system;

import net.plethora.bot.botapi.handler.jobhandler.parsers.VacancyFormatBuilder;
import net.plethora.bot.botapi.keyboards.kbjob.KeyboardJobChoiceVacancy;
import net.plethora.bot.cache.CacheVacancySearchUser;
import net.plethora.bot.model.Vacancy;
import net.plethora.bot.model.systemmodel.InfoForSearch;
import net.plethora.bot.model.systemmodel.SaveVacancyCell;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ShiftViewVacancy<T> {

    private final int NUMBER_NEXT = 1;
    private final int NUMBER_BACK = -1;

    private KeyboardJobChoiceVacancy keyboardJobChoiceVacancy;
    private CacheVacancySearchUser cacheVacancySearchUser;
    private VacancyFormatBuilder vacancyFormatBuilder;

    public ShiftViewVacancy(KeyboardJobChoiceVacancy keyboardJobChoiceVacancy, CacheVacancySearchUser cacheVacancySearchUser,
                            VacancyFormatBuilder vacancyFormatBuilder) {
        this.keyboardJobChoiceVacancy = keyboardJobChoiceVacancy;
        this.cacheVacancySearchUser = cacheVacancySearchUser;
        this.vacancyFormatBuilder = vacancyFormatBuilder;

    }

    public List<T> view(long chatId, int messageId, InfoForSearch info, boolean next, boolean back) {

        List<T> msgForSend = new ArrayList<>();        //список сообщений для отправки
        SaveVacancyCell saveVacancyCell = getSave(info);   //получаем ячейку сохранения с вакансиями
System.out.println("Ячейка сохранения открыта" + saveVacancyCell);
System.out.println("Содержание вакансий: " + Arrays.toString(saveVacancyCell.getVacancies()));
//        if (saveVacancyCell == null) {    //если ячейка отсутствует её необходимо воссоздать
//            saveVacancyCell = new SaveVacancyCell();
//
//        }
        //ТЕКУЩИЙ
        if (!next && !back) {
            System.out.println("Запрос на получение актуальной ваканссии");
            msgForSend = actualVacancy(chatId, messageId, saveVacancyCell, info.getId());
        }
        //СЛЕДУЮЩИЙ
        else if (next && !back) {
            msgForSend = shift(chatId, messageId, NUMBER_NEXT, saveVacancyCell, info.getId());
        }
        //ПРЕДЫДУЩИЙ
        else if (back && !next) {
            msgForSend = shift(chatId, messageId, NUMBER_BACK, saveVacancyCell,info.getId());
        }
        System.out.println("Отправляю сообщение " + msgForSend.size());
        return msgForSend;
    }

    /**
     * Сдвиг материала назад<-->вперед
     */
    private List<T> shift(long chatId, int messageId, int shiftNumber, SaveVacancyCell saveVacancyCell,int idInfoForSearch) {
        List<T> msgForSend = new ArrayList<>();
        Vacancy[] vacancies = saveVacancyCell.getVacancies();
        for (int i = 0; i < saveVacancyCell.getVacancies().length; i++) {
            if (vacancies[i].getId() == saveVacancyCell.getSaveIdVacancy()) {
                saveVacancyCell.setSaveIdVacancy(vacancies[i + shiftNumber].getId());
                msgForSend.add((T) editMessageVacancy(chatId, messageId, i + shiftNumber, vacancies.length, vacancies[i + shiftNumber],idInfoForSearch));
                break;
            }
        }
        return msgForSend;
    }


    private List<T> actualVacancy(long chatId, int messageId, SaveVacancyCell saveVacancyCell, int idInfoSearch) {
        List<T> messageTexts = new ArrayList<>();

        int limit = saveVacancyCell.getVacancies().length;
        System.out.println("Размер листа вакансий составляет" + limit);
        int number = 0;

        for (Vacancy vacancy : saveVacancyCell.getVacancies()) {
            number++;
            if (saveVacancyCell.getSaveIdVacancy()==vacancy.getId()) {
                messageTexts.add((T) editMessageVacancy(chatId, messageId, number, limit, vacancy,idInfoSearch));
            } else {System.out.println("Не совпало");}

        }
        return messageTexts;
    }

    //EDIT TASK
    private EditMessageText editMessageVacancy(long chatId, int messageId, int number, int limit, Vacancy vacancy, int idInfoSearch) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText("[" + number + "/" + limit + "]\n" + vacancyFormatBuilder.getBuildTextVacancy(vacancy))
                .enableHtml(true)
                .disableWebPagePreview();
        editMessageText.setReplyMarkup(keyboardJobChoiceVacancy.keyboard(number, limit, vacancy.getUrlRespond(), vacancy.getUrlVacancy(),idInfoSearch));
        return editMessageText;
    }

    private SaveVacancyCell getSave(InfoForSearch infoForSearch) {
       return cacheVacancySearchUser.getCache().stream()
               .filter(i->i.getId() == infoForSearch.getId())
               .findFirst()
               .orElse(null);
    }
}