package net.plethora.bot.botapi.handler;

import lombok.SneakyThrows;
import net.plethora.bot.botapi.keyboards.KeyboardBot;
import net.plethora.bot.dao.DataAccessTask;
import net.plethora.bot.dao.DataAccessUser;
import net.plethora.bot.model.Task;
import net.plethora.bot.model.User;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class HandlerTaskMessage<T> {

    private DataAccessTask dataAccessTask;
    private DataAccessUser dataAccessUser;
    private KeyboardBot keyboardBot;

    public HandlerTaskMessage(DataAccessTask dataAccessTask, DataAccessUser dataAccessUser, KeyboardBot keyboardBot) {
        this.dataAccessTask = dataAccessTask;
        this.dataAccessUser = dataAccessUser;
        this.keyboardBot = keyboardBot;
    }

    @SneakyThrows
    public List<T> go(long chatId, String msgUser, User user) {
        List<T> msg = new ArrayList<>();
//TODO нужен выбор конкретной задачи типа "Ветвления_1"
//TODO нужен показ всех задач заданной темы
        //TODO пользователь должен иметь возможность стереть задачи с аккаунта
        List<String> idTaskUser = Arrays.asList(user.getReceivedTasks());   //список id решенных задач user
        List<String> newListTaskForUser = new ArrayList<>();   //новы список задач для user
        if (idTaskUser.size() != 0) {
            newListTaskForUser.addAll(idTaskUser);
        }

        if (checkSubject(msgUser)) {   //если прилетела тема задачи
            //TODO в идеале все задачи темы перебирать прямо в базе
            List<Task> allTasks = dataAccessTask.handleRequest(msgUser);  //все задачи на заданную тему
//TODO можно добавить кнопку далее и назад и изменить раздел(а на последней задаче убрать далее и на первой убрать назад)
            msg = (List<T>) getProblem(chatId, allTasks, idTaskUser, msgUser, user, newListTaskForUser);
            msg.add((T) new SendMessage(chatId, "Выберите раздел следующей задачи")
                    .setReplyMarkup(keyboardBot.inlineKeyboardSubjectTask()));
        }

        else if (msgUser.substring(0, 6).equals(":!awr{")) {   //если сообщение является ответом к задаче
            msg = (List<T>) getSolution(chatId, idTask(msgUser));  //вытягиваем id Task, получаем ответ и ложим его в список

        } else {
            msg.add((T) new SendMessage(chatId, "Неверная форма ввода, нажмите на нужный раздел выше или измените сервис"));
        }


        return msg;
    }


    private List<SendMessage> getProblem(long chatId, List<Task> allTasks, List<String> idTasksUser, String msgUser, User user, List<String> newListTaskForUser) {

        List<SendMessage> msg = new ArrayList<>();

        boolean check = false;          //проверка на наличие задачи в списке user
        for (Task task : allTasks) {
            if (idTasksUser.size() == 0) {  //если у user нет задач
                String problem = task.getProblem();
                msg.add(new SendMessage(chatId, problem).setReplyMarkup(keyboard(task.getId())));
                check = true;
            } else {
                int count = 0; //Наличие совпадеий
                for (String id : idTasksUser) {
                    if (id.equals(task.getId())) {
                        count++;
                    }
                }
                if (count == 0) { //если совпадени не было
                    String problem = task.getProblem();
                    msg.add(new SendMessage(chatId, problem).setReplyMarkup(keyboard(task.getId())));
                    check = true;
                }
            }
            if (check) {  //если не надено(true)
                newListTaskForUser.add(task.getId()); //добавляем в список
                String[] newList = newListTaskForUser.toArray(new String[0]); //создаем массив из списка
                dataAccessUser.editUser(user, newList);  //меняем массив пользователя на новый
                break;
            }
        }
        if (msg.size() == 0) { //если ничего не добавлено
            msg.add(new SendMessage(chatId, "Вы выполнили все задачи в разделе " + msgUser));
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
        Task task = dataAccessTask.findById(idTask);
        System.out.println(task);
        String urlDownload = task.getSolution();
        File file = downloadFile(urlDownload, task.getFileName());

        List<SendDocument> solution = new ArrayList<>();
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(idChat);
        sendDocument.setDocument(file);
        solution.add(sendDocument);

        return solution;
    }

    /**
     * Генерируем кнопку для получения решения задачи
     *
     * @param idTask id задачи
     * @return готовую кнопку для сообщения
     */
    private InlineKeyboardMarkup keyboard(String idTask) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(); //клава
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();  //ряды
        List<InlineKeyboardButton> row = new ArrayList<>(); //ряд

        InlineKeyboardButton keyboardButton = new InlineKeyboardButton("Решение"); //сама кнопка
        keyboardButton.setCallbackData(":!awr{" + idTask + "}");  //ответка в виде id задачи и небольшога шифра
        row.add(keyboardButton);
        rows.add(row);

        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
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
        //Загрузка картинки
        //TODO указать относительный путь
        //TODO Придумать как после отправки удалить файл
        //String filePath = "resources" + File.pathSeparator + "filetask";
        //String filePath = "./resources/filetask";
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
                return true;
        }
        return false;
    }

    /**
     * @param msgUser сообщение пользователя
     * @return готовый id задачи
     */
    private String idTask(String msgUser) {

        Pattern regex = Pattern.compile("\\{([^}]*)"); //вытягиваем id из скобок
        Matcher regexMatcher = regex.matcher(msgUser);

        String idTask = null;
        while (regexMatcher.find()) {
            idTask = (regexMatcher.group());
            idTask = idTask.substring(1);//удаляем первый символ "{"
        }

        return idTask;
    }

}