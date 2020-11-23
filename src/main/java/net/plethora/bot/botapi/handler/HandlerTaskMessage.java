package net.plethora.bot.botapi.handler;

import lombok.SneakyThrows;
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

    public HandlerTaskMessage(DataAccessTask dataAccessTask, DataAccessUser dataAccessUser) {
        this.dataAccessTask = dataAccessTask;
        this.dataAccessUser = dataAccessUser;
    }

    @SneakyThrows
    public List<T> go(long chatId, String msgUser, User user) {
        List<T> msg = new ArrayList<>();
        List<String> idTaskUser = Arrays.asList(user.getReceivedTasks());
//TODO раскидать по методам
        List<String> newListForUser = new ArrayList<>();
        if (idTaskUser.size() != 0) {
            newListForUser.addAll(idTaskUser);
        }
        if (checkSubject(msgUser)) {   //если прилетела тема задачи
            List<Task> tasks = dataAccessTask.handleRequest(msgUser);

            boolean check = false;
            for (Task task : tasks) {
                if (idTaskUser.size() == 0) {
                    String problem = task.getProblem();
                    msg.add((T) new SendMessage(chatId, problem).setReplyMarkup(keyboard(task.getId())));
                    check = true;
                } else {
                    for (String id : idTaskUser) {
                        if (!id.equals(task.getId())) {
                            String problem = task.getProblem();
                            msg.add((T) new SendMessage(chatId, problem).setReplyMarkup(keyboard(task.getId())));
                            check = true;
                            break;
                        }
                    }
                }
                if (check) {
                    newListForUser.add(task.getId());
                    // idTaskUser.add(task.getId());
                    String[] newList = newListForUser.toArray(new String[0]);
                    dataAccessUser.editUser(user, newList);
                    break;
                }
            }
            check = false;
            return msg;
        }
        if (msgUser.substring(0, 6).equals(":!awr{")) {   //нужна регулярка на проверку шифра и достать id от туда
            String idTask = idTask(msgUser);
            System.out.println("Поиск " + idTask);
            msg = getSolution(chatId, idTask);

        } else {
            msg.add((T) new SendMessage(chatId, "Напиши дай задачу"));
        }


        return msg;
    }


//    public List<T> getProblem(long idChat) {
//
//        List<SendMessage> problem = new ArrayList<>();
//        SendMessage sendMessage = new SendMessage(idChat, "Это задача").setReplyMarkup(keyboard());
//        problem.add(sendMessage);
//        return (List<T>) problem;
//    }

    private List<T> getSolution(long idChat, String idTask) throws IOException {
        Task task = dataAccessTask.findById(idTask);
        System.out.println(task);
        String urlDownload = task.getSolution();
        File file = downloadFile(urlDownload, task.getFileName());

        List<SendDocument> solution = new ArrayList<>();
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(idChat);
        sendDocument.setDocument(file);
        solution.add(sendDocument);

        return (List<T>) solution;
    }

    private InlineKeyboardMarkup keyboard(String idTask) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton keyboardButton = new InlineKeyboardButton("Решение");
        keyboardButton.setCallbackData(":!awr{" + idTask + "}");  //ответка в виде id задачи и небольшога шифра
        row.add(keyboardButton);
        rows.add(row);

        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }

    private File downloadFile(String url, String fileName) throws IOException {
        //Загрузка картинки
        //String filePath = "resources" + File.pathSeparator + "filetask";
        //String filePath = "./resources/filetask";
        String filePath = "F:\\atumBot\\src\\main\\resources\\filetask";
        File file = new File(filePath + "\\" + fileName);
        int bufferSize = 1;

        URL connection = new URL(url);
        HttpURLConnection urlconn;
        urlconn = (HttpURLConnection) connection.openConnection();
        urlconn.setRequestMethod("GET");
        urlconn.connect();
        InputStream in = urlconn.getInputStream();
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

    private boolean checkSubject(String msgUser) {

        switch (msgUser) {
            case "ветвления":
                return true;

        }
        return false;
    }

    private String idTask(String msgUser) {

        Pattern regex = Pattern.compile("\\{([^}]*)");
        Matcher regexMatcher = regex.matcher(msgUser);

        String idTask = null;
        while (regexMatcher.find()) {
            idTask = (regexMatcher.group());
            idTask = idTask.substring(1);
        }

        return idTask;
    }


}
