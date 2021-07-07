package net.plethora.bot.botapi.handler;

import net.plethora.bot.botapi.keyboards.kbask.KeyboardAskSelect;
import net.plethora.bot.botapi.system.Cutter;
import net.plethora.bot.dao.DataAccessAnswer;
import net.plethora.bot.model.Answer;
import net.plethora.bot.service.PhrasesService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class HandlerAskMessage {

    private Answer answer;
    private final DataAccessAnswer dao;
    private PhrasesService phrasesService;
    private Cutter cutter;
    private KeyboardAskSelect keyboardAskSelect;

    public HandlerAskMessage(DataAccessAnswer dao, PhrasesService phrasesService, Cutter cutter,
                             KeyboardAskSelect keyboardAskSelect) {
        this.dao = dao;
        this.phrasesService = phrasesService;
        this.cutter = cutter;
        this.keyboardAskSelect = keyboardAskSelect;
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

        return dao.handleRequest(cutter.cut(ask));//ищем в базе через метод репозитория и присваиваем документ
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
            sendMessage.setReplyMarkup(keyboardAskSelect.getInlineKeyboardMarkup(answer));
        }
        return sendMessage;
    }
}