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
import java.util.List;

@Component
public class ShiftViewVacancy<T> {
    //TODO может произойти такое что количетво вакансий будет меньше чем до удаления
    //TODO и при новом создании ячейки id станет за пределы списка
    //TODO в таком случае нужно указать самый последний элемент!!!!!!!!!!!
    private KeyboardJobChoiceVacancy keyboardJobChoiceVacancy;
    private CacheVacancySearchUser cacheVacancySearchUser;
    private VacancyFormatBuilder vacancyFormatBuilder;

    public ShiftViewVacancy(KeyboardJobChoiceVacancy keyboardJobChoiceVacancy, CacheVacancySearchUser cacheVacancySearchUser,
                            VacancyFormatBuilder vacancyFormatBuilder) {
        this.keyboardJobChoiceVacancy = keyboardJobChoiceVacancy;
        this.cacheVacancySearchUser = cacheVacancySearchUser;
        this.vacancyFormatBuilder = vacancyFormatBuilder;

    }

    public List<T> view(long chatId, int messageId, InfoForSearch info, SaveVacancyCell saveVacancyCell, boolean next, boolean back) {

        List<T> msgForSend = new ArrayList<>();        //список сообщений для отправки

        //ТЕКУЩИЙ
        if (!next && !back) {
            msgForSend = shift(chatId, messageId, 0, saveVacancyCell, info);
        }
        //СЛЕДУЮЩИЙ
        else if (next && !back) {
            msgForSend = shift(chatId, messageId, 1, saveVacancyCell, info);
        }
        //ПРЕДЫДУЩИЙ
        else if (back && !next) {
            msgForSend = shift(chatId, messageId, -1, saveVacancyCell,info);
        }

        return msgForSend;
    }

    /**
     * Сдвиг вакансии:  назад<--текущий-->вперед
     */
    private List<T> shift(long chatId, int messageId, int shiftNumber, SaveVacancyCell saveVacancyCell,InfoForSearch infoForSearch) {
        List<T> msgForSend = new ArrayList<>();
        Vacancy[] vacancies = saveVacancyCell.getVacancies();
        for (int i = 0; i < saveVacancyCell.getVacancies().length; i++) {
            if (vacancies[i].getId() == infoForSearch.getIdVacancy()) {
                msgForSend.add((T) editMessageVacancy(chatId, messageId, (i+1) + shiftNumber, vacancies.length, vacancies[i + shiftNumber],infoForSearch));
                break;
            }
        }
        return msgForSend;
    }

    //EDIT TASK
    private EditMessageText editMessageVacancy(long chatId, int messageId, int number, int limit, Vacancy vacancy, InfoForSearch infoForSearch) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText("[" + number + "/" + limit + "]\n" + vacancyFormatBuilder.getBuildTextVacancy(vacancy))
                .enableHtml(true)
                .disableWebPagePreview();
        editMessageText.setReplyMarkup(keyboardJobChoiceVacancy.keyboard(number, limit, vacancy, infoForSearch));
        return editMessageText;
    }

    private SaveVacancyCell getSave(InfoForSearch infoForSearch) {
       return cacheVacancySearchUser.getCache().stream()
               .filter(i->i.getId() == infoForSearch.getId())
               .findFirst()
               .orElse(null);
    }
}