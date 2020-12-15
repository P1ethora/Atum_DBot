package net.plethora.bot.botapi.system;

import net.plethora.bot.botapi.keyboards.kbjob.KeyboardJobChoiceVacancy;
import net.plethora.bot.cache.CacheVacancySearchUser;
import net.plethora.bot.model.Vacancy;
import net.plethora.bot.model.systemmodel.InfoForSearch;
import net.plethora.bot.model.systemmodel.SaveVacancyCell;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.ArrayList;
import java.util.List;

@Component
public class ShiftViewVacancy<T> {

    private final int NUMBER_NEXT = 1;
    private final int NUMBER_BACK = -1;

    private KeyboardJobChoiceVacancy keyboardJobChoiceVacancy;
    private CacheVacancySearchUser cacheVacancySearchUser;

    public ShiftViewVacancy(KeyboardJobChoiceVacancy keyboardJobChoiceVacancy, CacheVacancySearchUser cacheVacancySearchUser) {
        this.keyboardJobChoiceVacancy = keyboardJobChoiceVacancy;
        this.cacheVacancySearchUser = cacheVacancySearchUser;

    }

    public List<T> view(long chatId, int messageId, InfoForSearch info, boolean next, boolean back) {

        List<T> msgForSend = new ArrayList<>();        //список сообщений для отправки
        SaveVacancyCell saveVacancyCell = getSave(info);   //получаем ячейку сохранения с вакансиями

        if (saveVacancyCell == null) {    //если ячейка отсутствует её необходимо воссоздать
            saveVacancyCell = new SaveVacancyCell();

        }
        //ТЕКУЩИЙ
        if (!next && !back) {
            msgForSend = actualVacancy(chatId, messageId, saveVacancyCell, String.valueOf(info.getId()));
        }
        //СЛЕДУЮЩИЙ
        else if (next && !back) {
            msgForSend = shift(chatId, messageId, NUMBER_NEXT, saveVacancyCell, String.valueOf(info.getId()));
        }
        //ПРЕДЫДУЩИЙ
        else if (back && !next) {
            msgForSend = shift(chatId, messageId, NUMBER_BACK, saveVacancyCell, String.valueOf(info.getId()));
        }
        return msgForSend;
    }

    /**
     * Сдвиг материала назад<-->вперед
     */
    private List<T> shift(long chatId, int messageId, int shiftNumber, SaveVacancyCell saveVacancyCell,String idInfoForSearch) {
        List<T> msgForSend = new ArrayList<>();
        Vacancy[] vacancies = saveVacancyCell.getVacancies();
        for (int i = 0; i < saveVacancyCell.getVacancies().length; i++) {
            if (vacancies[i].getId().equals(saveVacancyCell.getActualIdVacancy())) {
                saveVacancyCell.setActualIdVacancy(vacancies[i + shiftNumber].getId());
                msgForSend.add((T) editMessageVacancy(chatId, messageId, i + shiftNumber, vacancies.length, vacancies[i + shiftNumber],idInfoForSearch));
                break;
            }
        }
        return msgForSend;
    }


    private List<T> actualVacancy(long chatId, int messageId, SaveVacancyCell saveVacancyCell, String idInfoSearch) {
        List<T> messageTexts = new ArrayList<>();

        int limit = saveVacancyCell.getVacancies().length;
        int number = 0;

        for (Vacancy vacancy : saveVacancyCell.getVacancies()) {
            number++;
            if (saveVacancyCell.getActualIdVacancy().equals(vacancy.getId())) {
                messageTexts.add((T) editMessageVacancy(chatId, messageId, number, limit, vacancy,idInfoSearch));
            }
        }

        return messageTexts;
    }

    //EDIT TASK
    private EditMessageText editMessageVacancy(long chatId, int messageId, int number, int limit, Vacancy vacancy, String idInfoSearch) {
        EditMessageText editMessageTask = new EditMessageText();
        editMessageTask.setChatId(chatId);
        editMessageTask.setMessageId(messageId);
        editMessageTask.setText("[" + number + "/" + limit + "]\n" + buildTextVacancy(vacancy));
        editMessageTask.setReplyMarkup(keyboardJobChoiceVacancy.keyboard(number, limit, vacancy.getUrlRespond(), vacancy.getUrlVacancy(),idInfoSearch));
        return editMessageTask;
    }

    private StringBuilder buildTextVacancy(Vacancy vacancy) {
        StringBuilder stringBuilder = new StringBuilder();


        return stringBuilder;
    }

    private SaveVacancyCell getSave(InfoForSearch infoForSearch) {
        return cacheVacancySearchUser.getCache().get(1/*infoForSearch.getPositionCache()*/);
    }

}