package net.plethora.bot.botapi.handler.handask;

import net.plethora.bot.dao.DataAccessAnswer;
import net.plethora.bot.model.Answer;
import net.plethora.bot.service.PhrasesService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;


@Component
public class HandlerAskMessage<T> {

    private Answer answer;
    private final DataAccessAnswer dao;
    private PhrasesService phrasesService;

    public HandlerAskMessage(DataAccessAnswer dao, PhrasesService phrasesService) {
        this.dao = dao;
        this.phrasesService = phrasesService;
    }

    /**
     * Получаем сообщение из базы для клиента по запросу
     *
     * @param ask сообщение запроса
     * @return String содержащий ответ
     */
    private String getStrAnswer(String ask) {
        answer = assignAnswer(ask);  //присваиваем объект из mongodb
        return answer != null ? answer.getAnswer() : phrasesService.getMessage("phrase.AskNegativeAnswer");
    }

    /**
     * Делаем запрос в базу данных
     *
     * @param ask строка запроса
     */
    private Answer assignAnswer(String ask) {
        return dao.handleRequest(ask);//ищем в базе через метод репозитория и присваиваем документ
    }

    /**
     * Формируем сообщение
     *
     * @param chatId id сообщения запроса
     * @param ask    строка запроса
     * @return sendMessage готовое сообщение
     */
    public SendMessage sendMessage(long chatId, String ask) {
        SendMessage sendMessage = new SendMessage(chatId, getStrAnswer(ask));
        if (answer != null && answer.getKeyWords().length > 0) {
            sendMessage.setReplyMarkup(getInlineKeyboardMarkup());
        } //добавляем клавиатуру
        return sendMessage;
    }


    /**
     * Создаем клавиатуру под сообщением на основе ключевых слов в документе Mongodb
     *
     * @return keyboardMsg  - готовая клавиатура
     */
    private InlineKeyboardMarkup getInlineKeyboardMarkup() {

        InlineKeyboardMarkup keyBoardMsg = new InlineKeyboardMarkup();   //наша клава под msg
        List<List<InlineKeyboardButton>> rows = new ArrayList<>(); //формируем ряды

        fillRows(rows);

        int countGlobal = 0; //счетчик всех по порядку ключевых слов
        for (List<InlineKeyboardButton> list : rows) {
            int count = 0;  //внутренний счетчик на количество кнопок в ряду
            while (countGlobal < answer.getKeyWords().length) {
                if (count + 1 > 3) {
                    break;
                }   //если 4 кнопки добавлены выходим
                InlineKeyboardButton button = new InlineKeyboardButton(answer.getKeyWords()[countGlobal]);    //текст на кнопке
                button.setCallbackData(answer.getKeyWords()[countGlobal]); //текст содержащийся в колбеке
                count++;
                countGlobal++;
                list.add(button);
            }
        }

        keyBoardMsg.setKeyboard(rows);

        return keyBoardMsg;
    }

    /**
     * Наполняем массив пустыми рядами
     *
     * @param rows лист с кнопками
     */
    private void fillRows(List<List<InlineKeyboardButton>> rows) {
        int numberRows = (answer.getKeyWords().length / 3) + 1;//делим на 3 и округляем в большую сторону
        for (int i = 0; i < numberRows; i++) {   //заполняем лист пустыми рядами
            rows.add(new ArrayList<>());
        }
    }
}