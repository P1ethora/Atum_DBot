package net.plethora.bot.botapi.handler.handtask;

import lombok.SneakyThrows;
import net.plethora.bot.botapi.keyboards.KeyboardBot;
import net.plethora.bot.botapi.state.TaskState;
import net.plethora.bot.dao.DataAccessTask;
import net.plethora.bot.dao.DataAccessUser;
import net.plethora.bot.model.Task;
import net.plethora.bot.model.User;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import javax.security.auth.Subject;
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
//TODO зашифровать сосстояние тему и тд то бы из любого состояния можно было кнопеой переключатся автоматически
        if (msgUser.substring(0, 9).equals("%n->ex!t{")) {
            //Определить тему, метод есть
            String subject = getValueMsg(msgUser);
            for (SubjectTaskUser subjectTaskUser : user.getSubjectTask()) {
                if (subjectTaskUser.getSubjectTask().equals(subject)) {
                  msg = (List<T>) getProblem(chatId, subject, user, true, false);
                }
            }

        }
        if (msgUser.substring(0, 9).equals("%b->ac!k{")) {

            String subject = getValueMsg(msgUser);
            for (SubjectTaskUser subjectTaskUser : user.getSubjectTask()) {
                if (subjectTaskUser.getSubjectTask().equals(subject)) {
                  msg = (List<T>) getProblem(chatId, subject, user, false, true);
                }
            }
        }

        if (checkSubject(msgUser)) {   //если прилетела тема задачи
            // if(u)
            //TODO в идеале все задачи темы перебирать прямо в базе
//TODO можно добавить кнопку далее и назад и изменить раздел(а на последней задаче убрать далее и на первой убрать назад)
            msg = (List<T>) getProblem(chatId, msgUser, user, false, false);
            //msg.add((T) new SendMessage(chatId, "Выберите раздел следующей задачи")
            //     .setReplyMarkup(keyboardBot.inlineKeyboardSubjectTask()));
        } else if (msgUser.substring(0, 6).equals(":!awr{")) {   //если сообщение является ответом к задаче
            msg = (List<T>) getSolution(chatId, getValueMsg(msgUser));  //вытягиваем id Task, получаем ответ и ложим его в список

        } else {
            if(msg.size()==0)
            msg.add((T) new SendMessage(chatId, "Неверная форма ввода, нажмите на нужный раздел выше или измените сервис"));
        }


        return msg;
    }


    private List<SendMessage> getProblem(long chatId, String subject, User user, boolean next, boolean back) {

        List<Task> allTasks = dataAccessTask.handleRequest(subject);  //все задачи на заданную тему
        List<SubjectTaskUser> allSubjectUsers = Arrays.asList(user.getSubjectTask());   //список id решенных задач user
        List<SubjectTaskUser> newListSubjectTaskForUser = new ArrayList<>();   //новы список задач для user

        if (allSubjectUsers.size() != 0) { //хорошо бы это в конец присунуть уже редактированный allSubjectUser
            newListSubjectTaskForUser.addAll(allSubjectUsers);
        }

        List<SendMessage> msgForSend = new ArrayList<>();

        if (!next && !back) {

            msgForSend = getTask(chatId, subject, user, allSubjectUsers, allTasks, newListSubjectTaskForUser);

        } else if (next&&!back) {
            boolean check = false;
            for (SubjectTaskUser subjectTaskUser : newListSubjectTaskForUser) {
                if (subjectTaskUser.getSubjectTask().equals(subject)) {
                    for (int i = 0; i < allTasks.size(); i++) {
                        if (subjectTaskUser.getIdTask().equals(allTasks.get(i).getId())) {
                            System.out.println("Был ->  " + subjectTaskUser);
                            subjectTaskUser.setIdTask(allTasks.get(i + 1).getId());
                            System.out.println("Стал -> " + subjectTaskUser);
                            System.out.println("Новый список  -> " + newListSubjectTaskForUser);
                            updateUser(user, newListSubjectTaskForUser);
                            check = true;
                            break;
                        }
                    }
                   msgForSend = getTask(chatId, subject, user, allSubjectUsers, allTasks, newListSubjectTaskForUser);
                    if(check){break;}
                }
            }
        } else if (back&&!next) {
            for (SubjectTaskUser subjectTaskUser : newListSubjectTaskForUser) {
                if (subjectTaskUser.getSubjectTask().equals(subject)) {
                    for (int i = 0; i < allTasks.size(); i++) {
                        if (subjectTaskUser.getIdTask().equals(allTasks.get(i).getId())) {
                            subjectTaskUser.setIdTask(allTasks.get(i - 1).getId());
                            updateUser(user, newListSubjectTaskForUser);
                            break;
                        }
                    }
                   msgForSend = getTask(chatId, subject, user, allSubjectUsers, allTasks, newListSubjectTaskForUser);
                }
            }
        }
        return msgForSend;
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
    private InlineKeyboardMarkup keyboard(String idTask,String subject) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(); //клава
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();  //ряды
        List<InlineKeyboardButton> row = new ArrayList<>(); //ряд
        List<InlineKeyboardButton> row1 = new ArrayList<>(); //ряд

        InlineKeyboardButton keyboardButton = new InlineKeyboardButton("Решение"); //сама кнопка
        //TODO убрать кнопки если первая задача или последняя
        InlineKeyboardButton keyboardButtonBack = new InlineKeyboardButton("Назад"); //сама кнопка
        InlineKeyboardButton keyboardButtonNext = new InlineKeyboardButton("Далее"); //сама кнопка

        keyboardButtonBack.setCallbackData("%b->ac!k{" + subject+"}");
        keyboardButtonNext.setCallbackData("%n->ex!t{"+ subject +"}");
        keyboardButton.setCallbackData(":!awr{" + idTask + "}");  //ответка в виде id задачи и небольшога шифра
        row1.add(keyboardButton);
        row.add(keyboardButtonBack);
        row.add(keyboardButtonNext);
        rows.add(row);
        rows.add(row1);

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
                return true;
        }
        return false;
    }

    /**
     * @param msgUser сообщение пользователя
     * @return готовый id задачи
     */
    private String getValueMsg(String msgUser) {

        Pattern regex = Pattern.compile("\\{([^}]*)"); //вытягиваем id из скобок
        Matcher regexMatcher = regex.matcher(msgUser);

        String valueMsg = null;
        while (regexMatcher.find()) {
            valueMsg = (regexMatcher.group());
            valueMsg = valueMsg.substring(1);//удаляем первый символ "{"
        }

        return valueMsg;
    }

    private List<SendMessage> getTask(long chatId, String subject, User user, List<SubjectTaskUser> allSubjectUsers, List<Task> allTasks, List<SubjectTaskUser> newListSubjectTaskForUser) {
        List<SendMessage> msgForSend = new ArrayList<>();

        boolean check = false;
        int count = 0;
        for (SubjectTaskUser subjectTaskUser : allSubjectUsers) {
            if (subjectTaskUser.getSubjectTask().equals(subject)) {
                for (Task task1 : allTasks) { //по сути можно просто вытянуть задачу из базы монго
                    if (subjectTaskUser.getIdTask().equals(task1.getId())) {
                        count++;
                        msgForSend.add(new SendMessage(chatId, task1.getProblem()).setReplyMarkup(keyboard(task1.getId(),subject)));
                        break;
                    }
                }
            }
        }
        if (count == 0) {
            System.out.println("Совпадений не было");
            newListSubjectTaskForUser.add(new SubjectTaskUser(allTasks.get(0).getId(), allTasks.get(0).getSubject()));
            msgForSend.add(new SendMessage(chatId, allTasks.get(0).getProblem()).setReplyMarkup(keyboard(allTasks.get(0).getId(),subject)));
            check = true;
        }
        if (check) {
            updateUser(user, newListSubjectTaskForUser);
        }

        return msgForSend;
    }

    private void updateUser(User user, List<SubjectTaskUser> newListSubjectTaskForUser) {
        dataAccessUser.editUser(user, newListSubjectTaskForUser.toArray(new SubjectTaskUser[0]));  //меняем массив пользователя на новый
    }

}