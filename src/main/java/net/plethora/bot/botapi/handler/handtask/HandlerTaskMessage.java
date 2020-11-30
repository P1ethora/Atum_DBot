package net.plethora.bot.botapi.handler.handtask;

import lombok.SneakyThrows;
import net.plethora.bot.botapi.keyboards.KeyboardSubjectTask;
import net.plethora.bot.botapi.keyboards.KeyboardTaskSelect;
import net.plethora.bot.dao.DataAccessTask;
import net.plethora.bot.dao.DataAccessUser;
import net.plethora.bot.model.Task;
import net.plethora.bot.model.User;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.*;
import java.net.HttpURLConnection;
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
    private KeyboardSubjectTask keyboardSubjectTask;

    public HandlerTaskMessage(DataAccessTask dataAccessTask, DataAccessUser dataAccessUser, KeyboardSubjectTask keyboardSubjectTask) {
        this.dataAccessTask = dataAccessTask;
        this.dataAccessUser = dataAccessUser;
        this.keyboardSubjectTask = keyboardSubjectTask;
    }

    @SneakyThrows
    public List<T> go(long chatId, String msgUser, User user, int messageId) {
        List<T> msg = new ArrayList<>();
//TODO зашифровать сосстояние тему и тд то бы из любого состояния можно было кнопеой переключатся автоматически
        if (msgUser.length() > 9 && msgUser.substring(0, 9).equals("%n->ex!t{")) {

            String subject = getValueMsg(msgUser);
            for (SubjectTaskUser subjectTaskUser : user.getSubjectTask()) {
                if (subjectTaskUser.getSubjectTask().equals(subject)) {
                    msg = getProblem(chatId, subject, user, messageId, true, false);
                }
            }

        } else if (msgUser.length() > 9 && msgUser.substring(0, 9).equals("%b->ac!k{")) {
            String subject = getValueMsg(msgUser);
            for (SubjectTaskUser subjectTaskUser : user.getSubjectTask()) {
                if (subjectTaskUser.getSubjectTask().equals(subject)) {
                    msg = getProblem(chatId, subject, user, messageId, false, true);
                }
            }
        } else if (checkSubject(msgUser)) {   //если прилетела тема задачи
            msg = getProblem(chatId, msgUser, user, messageId, false, false);
        } else if (msgUser.length() > 6 && msgUser.substring(0, 6).equals(":!awr{")) {   //если сообщение является ответом к задаче
            msg = (List<T>) getSolution(chatId, getValueMsg(msgUser));  //вытягиваем id Task, получаем ответ и ложим его в список
        } else if (msgUser.equals(":выбор")) {
            msg.add((T) new SendMessage(chatId, "Выберите раздел следующей задачи")
                    .setReplyMarkup(keyboardSubjectTask.inlineKeyboardSubjectTask()));
        } else {
            msg.add((T) new SendMessage(chatId, "Неверная форма ввода, нажмите на нужный раздел выше или измените сервис"));
        }
        return msg;
    }


    private List<T> getProblem(long chatId, String subject, User user, int messageId, boolean next, boolean back) {

        List<Task> allTasks = dataAccessTask.handleRequest(subject);  //все задачи на заданную тему
        List<SubjectTaskUser> allSubjectUsers = Arrays.asList(user.getSubjectTask());   //список id решенных задач user
        List<SubjectTaskUser> newListSubjectTaskForUser = new ArrayList<>();   //новы список задач для user

        if (allSubjectUsers.size() != 0) { //хорошо бы это в конец присунуть уже редактированный allSubjectUser
            newListSubjectTaskForUser.addAll(allSubjectUsers);
        }

        List<T> msgForSend = new ArrayList<>();

        if (!next && !back) {

            msgForSend = (List<T>) createMessageTask(chatId, subject, user, allTasks, newListSubjectTaskForUser);

        } else if (next && !back) {
            boolean check = false;
            for (SubjectTaskUser subjectTaskUser : newListSubjectTaskForUser) {
                if (subjectTaskUser.getSubjectTask().equals(subject)) {

                    for (int i = 0; i < allTasks.size(); i++) {
                        if (subjectTaskUser.getIdTask().equals(allTasks.get(i).getId())) {
                            subjectTaskUser.setIdTask(allTasks.get(i + 1).getId());
                            updateUser(user, newListSubjectTaskForUser);
                            check = true;
                            break;
                        }
                    }
                    msgForSend = editMessage(messageId, chatId, subject, allTasks, allSubjectUsers);
                    if (check) {
                        break;
                    }
                }
            }
        } else if (back && !next) {
            for (SubjectTaskUser subjectTaskUser : newListSubjectTaskForUser) {
                if (subjectTaskUser.getSubjectTask().equals(subject)) {
                    for (int i = 0; i < allTasks.size(); i++) {
                        if (subjectTaskUser.getIdTask().equals(allTasks.get(i).getId())) {
                            subjectTaskUser.setIdTask(allTasks.get(i - 1).getId());
                            updateUser(user, newListSubjectTaskForUser);
                            break;
                        }
                    }
                    msgForSend = editMessage(messageId, chatId, subject, allTasks, allSubjectUsers);
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

        Pattern regex = Pattern.compile("\\{([^}]*)"); //вытягиваем id из скобок
        Matcher regexMatcher = regex.matcher(msgUser);

        String valueMsg = null;
        while (regexMatcher.find()) {
            valueMsg = (regexMatcher.group());
            valueMsg = valueMsg.substring(1);//удаляем первый символ "{"
        }

        return valueMsg;
    }

    /**
     * @param chatId                    id чата
     * @param subject                   тема задачи
     * @param user                      пользователь
     * @param allTasks                  все задачи в базе на заданную тему
     * @param newListSubjectTaskForUser новый список для пользователя
     * @return сообщение в списке, с нужной задачей
     */
    private List<SendMessage> createMessageTask(long chatId, String subject, User user, List<Task> allTasks, List<SubjectTaskUser> newListSubjectTaskForUser) {
        List<SendMessage> msgForSend = new ArrayList<>();

        int count = 0;
        int limit = allTasks.size();
        int numberTask = 0;
        for (SubjectTaskUser subjectTaskUser : user.getSubjectTask()) {
            if (subjectTaskUser.getSubjectTask().equals(subject)) {
                for (Task task1 : allTasks) {
                    numberTask++;
                    if (subjectTaskUser.getIdTask().equals(task1.getId())) {
                        count++;
                        msgForSend.add(new SendMessage(chatId, "[" + numberTask + "/" + limit + "] " + task1.getProblem())
                                .setReplyMarkup(new KeyboardTaskSelect(task1.getId(), subject, limit, numberTask).keyboard()));
                        break;
                    }
                }
            }
        }
        if (count == 0) {
            newListSubjectTaskForUser.add(new SubjectTaskUser(allTasks.get(0).getId(), allTasks.get(0).getSubject()));
            msgForSend.add(new SendMessage(chatId, allTasks.get(0).getProblem())
                    .setReplyMarkup(new KeyboardTaskSelect(allTasks.get(0).getId(), subject, limit, 1).keyboard()));
            updateUser(user, newListSubjectTaskForUser);
        }

        return msgForSend;
    }

    private List<T> editMessage(int messageId, long chatId, String subject, List<Task> allTasks, List<SubjectTaskUser> allSubjectUsers) {
        List<T> messageTexts = new ArrayList<>();
        EditMessageText editMessageText = new EditMessageText();

        int count = 0;
        int limit = allTasks.size();
        int numberTask = 0;
        for (SubjectTaskUser subjectTaskUser : allSubjectUsers) {
            if (subjectTaskUser.getSubjectTask().equals(subject)) {
                for (Task task1 : allTasks) {
                    numberTask++;
                    if (subjectTaskUser.getIdTask().equals(task1.getId())) {
                        count++;
                        editMessageText.setChatId(chatId);
                        editMessageText.setMessageId(messageId);
                        editMessageText.setText("[" + numberTask + "/" + limit + "] " + task1.getProblem());
                        editMessageText.setReplyMarkup(new KeyboardTaskSelect(task1.getId(), subject, limit, numberTask).keyboard());
                        messageTexts.add((T) editMessageText);
                        // messageTexts.add(new SendMessage(chatId, task1.getProblem()).setReplyMarkup(keyboard(task1.getId(), subject)));
                        break;
                    }
                }
            }
        }
        if (count == 0) {
            messageTexts.add((T) new SendMessage(chatId, "Неверно"));
//            newListSubjectTaskForUser.add(new SubjectTaskUser(allTasks.get(0).getId(), allTasks.get(0).getSubject()));
//            msgForSend.add(new SendMessage(chatId, allTasks.get(0).getProblem()).setReplyMarkup(keyboard(allTasks.get(0).getId(), subject)));
//            updateUser(user, newListSubjectTaskForUser);
        }
        return messageTexts;
    }

    private void updateUser(User user, List<SubjectTaskUser> newListSubjectTaskForUser) {
        dataAccessUser.editUser(user, newListSubjectTaskForUser.toArray(new SubjectTaskUser[0]));  //меняем массив пользователя на новый
    }

}