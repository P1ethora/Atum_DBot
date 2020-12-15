package net.plethora.bot.botapi.handler;

import lombok.SneakyThrows;
import net.plethora.bot.botapi.system.ShiftViewMaterial;
import net.plethora.bot.dao.DataAccessMaterialTask;
import net.plethora.bot.model.material.Task;
import net.plethora.bot.botapi.system.systemMessage.OptionTypeTaskMessage;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class HandlerTaskMessage<T> {

    private DataAccessMaterialTask dataAccessTask;
    private OptionTypeTaskMessage optionTypeTaskMessage;
    private ShiftViewMaterial shiftViewMaterial;

    public HandlerTaskMessage(DataAccessMaterialTask dataAccessTask, OptionTypeTaskMessage optionTypeTaskMessage,
                              ShiftViewMaterial shiftViewMaterial) {
        this.dataAccessTask = dataAccessTask;
        this.optionTypeTaskMessage = optionTypeTaskMessage;
        this.shiftViewMaterial = shiftViewMaterial;
    }

    @SneakyThrows
    public List<T> process(long chatId, String msgUser, int messageId) {
        List<T> msg = new ArrayList<>();
//TODO зашифровать сосстояние тему и тд то бы из любого состояния можно было кнопеой переключатся автоматически
        if (msgUser.length() > 9 && msgUser.substring(0, 9).equals("%n->ex!t{")) {
            msg = shiftViewMaterial.view(chatId, getValueMsg(msgUser), messageId, dataAccessTask, true, false);
        } else if (msgUser.length() > 9 && msgUser.substring(0, 9).equals("%b->ac!k{")) {
            msg = shiftViewMaterial.view(chatId, getValueMsg(msgUser), messageId, dataAccessTask, false, true);
        } else if (checkSubject(msgUser)) {   //если прилетела тема задачи
            msg = shiftViewMaterial.view(chatId, msgUser, messageId, dataAccessTask, false, false);
        } else if (msgUser.length() > 6 && msgUser.substring(0, 6).equals(":!awr{")) {   //если сообщение является ответом к задаче
            msg = (List<T>) getSolution(chatId, getValueMsg(msgUser));  //вытягиваем id Task, получаем ответ и ложим его в список
        } else if (msgUser.equals(":backtosubject")) {
            msg.add((T) optionTypeTaskMessage.editMessage(chatId, messageId));
        } else {
            msg.add((T) new SendMessage(chatId, "Неверная форма ввода, задачи предоставляются посредством выбора раздела"));
        }
        return msg;
    }

    /**
     * Получаем ответ на задачу в виде File SendDocument
     *
     * @param idChat нужный чат
     * @param idTask id задачи
     * @return список с файлом
     */
    private List<SendDocument> getSolution(long idChat, String idTask) {
        Task task = dataAccessTask.findById(idTask);   //вытягиваем из базы задачу по id
        List<SendDocument> solution = new ArrayList<>();
        SendDocument sendDocument = new SendDocument(); //объект для отправки пользователю
        sendDocument.setChatId(idChat);    //id чата
        sendDocument.setDocument(new InputFile().setMedia(task.getSolution()));  //скачиваем файл нашим методом и вносим в документ
        solution.add(sendDocument);   //добавляем в список

        return solution;
    }

    /**
     * Проверка является ли сообщение юзеря темой задачи
     *
     * @param msgUser сообщение юзера
     * @return да или нет
     */
    private boolean checkSubject(String msgUser) {

        switch (msgUser) {
            case "ветвления":
            case "линейные":
                return true;
        }
        return false;
    }

    /**
     * @param msgUser сообщение пользователя
     * @return готовый id задачи
     */
    private String getValueMsg(String msgUser) {
//TODO найти способ убрать символ { через регулярку
        Pattern regex = Pattern.compile("\\{([^}]*)"); //вытягиваем id из скобок
        Matcher regexMatcher = regex.matcher(msgUser);

        String valueMsg = null;
        while (regexMatcher.find()) {
            valueMsg = (regexMatcher.group());
            valueMsg = valueMsg.substring(1);//удаляем первый символ "{"
        }

        return valueMsg;
    }
}