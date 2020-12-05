package net.plethora.bot.botapi.handler;

import lombok.SneakyThrows;
import net.plethora.bot.botapi.system.ShiftView;
import net.plethora.bot.dao.DataAccessMaterialTask;
import net.plethora.bot.dao.DataAccessUser;
import net.plethora.bot.model.material.Task;
import net.plethora.bot.botapi.system.systemMessage.OptionTypeTaskMessage;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class HandlerTaskMessage<T> {

    private DataAccessMaterialTask dataAccessTask;
    private OptionTypeTaskMessage optionTypeTaskMessage;
    private ShiftView shiftView;

    public HandlerTaskMessage(DataAccessMaterialTask dataAccessTask, OptionTypeTaskMessage optionTypeTaskMessage,
                              ShiftView shiftView) {
        this.dataAccessTask = dataAccessTask;
        this.optionTypeTaskMessage = optionTypeTaskMessage;
        this.shiftView = shiftView;
    }

    @SneakyThrows
    public List<T> process(long chatId, String msgUser, int messageId) {
        List<T> msg = new ArrayList<>();
//TODO зашифровать сосстояние тему и тд то бы из любого состояния можно было кнопеой переключатся автоматически
        if (msgUser.length() > 9 && msgUser.substring(0, 9).equals("%n->ex!t{")) {
           msg = shiftView.view(chatId, getValueMsg(msgUser), messageId, dataAccessTask, true, false);
        } else if (msgUser.length() > 9 && msgUser.substring(0, 9).equals("%b->ac!k{")) {
           msg = shiftView.view(chatId, getValueMsg(msgUser), messageId, dataAccessTask, false, true);
        } else if (checkSubject(msgUser)) {   //если прилетела тема задачи
            msg = shiftView.view(chatId, msgUser, messageId, dataAccessTask, false, false);
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
     * @throws IOException
     */
    private List<SendDocument> getSolution(long idChat, String idTask) throws IOException {
        Task task = dataAccessTask.findById(idTask);   //вытягиваем из базы задачу по id
        List<SendDocument> solution = new ArrayList<>();
        SendDocument sendDocument = new SendDocument(); //объект для отправки пользователю
        String urlDownload = task.getSolution();    //ссылка для скачивания
        sendDocument.setChatId(idChat);    //id чата
        sendDocument.setDocument(downloadFile(urlDownload, task.getFileName()));  //скачиваем файл нашим методом и вносим в документ
        solution.add(sendDocument);   //добавляем в список

        return solution;
    }

    /**
     * Загружаем картинку из гуглДиска
     *
     * @param url      ссылка загрузки
     * @param fileName имя фала
     * @return готовый файл
     * @throws IOException
     */
    private File downloadFile(String url, String fileName) throws IOException {
        //TODO указать относительный путь
        //TODO Придумать как после отправки удалить файл
        String filePath = "F:\\atumBot\\src\\main\\resources\\filetask";
        File file = new File(filePath + "\\" + fileName);
        int bufferSize = 1;

        URL connection = new URL(url);
        HttpURLConnection urlConn = (HttpURLConnection) connection.openConnection();
        urlConn.setRequestMethod("GET");
        urlConn.connect();
        InputStream in = urlConn.getInputStream();
        OutputStream writer = new FileOutputStream(file);
        byte[] buffer = new byte[bufferSize];
        int c = in.read(buffer);
        while (c > 0) {
            writer.write(buffer, 0, c);
            c = in.read(buffer);
        }
        writer.flush();
        writer.close();
        in.close();

        return file;
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