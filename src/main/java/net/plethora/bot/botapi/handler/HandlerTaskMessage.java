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

@Component
public class HandlerTaskMessage<T> {

    private DataAccessTask dataAccessTask;
    private DataAccessUser dataAccessUser;

    public HandlerTaskMessage(DataAccessTask dataAccessTask,DataAccessUser dataAccessUser){
        this.dataAccessTask = dataAccessTask;
        this.dataAccessUser = dataAccessUser;
    }

    @SneakyThrows
    public List<T> go(long chatId, String msgUser) {
        List<T> msg = null;
User user = dataAccessUser.findUser("fdfdfdfdfdf").get();
List<String> idTaskUser = Arrays.asList(user.getReceivedTasks());
        if(checkSubject(msgUser)){   //если прилетела тема задачи
            List<Task> tasks = dataAccessTask.handleRequest(msgUser);
            boolean check = false;
            for (Task task:tasks) {
                for (String id:idTaskUser) {
                    if(!id.equals(task.getId())){
                        String problem = task.getProblem();
                        msg.add((T) new SendMessage(chatId,problem));
                        check = true;
                        break;
                    }
                }
                        if(check){break;}
            }
            check = false;
        }

        if (msgUser.equals("дай задачу")) {
            msg = getProblem(chatId);

        }
        else if (msgUser.equals("solution")) {
            msg = getSolution(chatId);

        } else {
            msg.add((T) new SendMessage(chatId, "Напиши дай задачу"));
        }


        return msg;
    }


    public List<T> getProblem(long idChat) {

        List<SendMessage> problem = new ArrayList<>();
        SendMessage sendMessage = new SendMessage(idChat, "Это задача").setReplyMarkup(keyboard());
        problem.add(sendMessage);
        return (List<T>) problem;
    }

    public List<T> getSolution(long idChat) throws IOException {
        //String path = "E:\\";
        //String fileName = "solution.txt";
String urlDownload = "https://drive.google.com/uc?export=download&id=1uNSHPkgEu9F36tt1TmiNnkXwyEXRbgAc";
        File file = downloadFile(urlDownload + "1uNSHPkgEu9F36tt1TmiNnkXwyEXRbgAc");
        List<SendDocument> solution = new ArrayList<>();
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(idChat);
        sendDocument.setDocument(file);
        solution.add(sendDocument);

        return (List<T>) solution;
    }

    private InlineKeyboardMarkup keyboard() {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton keyboardButton = new InlineKeyboardButton("Решение");
        keyboardButton.setCallbackData("solution");
        row.add(keyboardButton);
        rows.add(row);

        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }

    private File downloadFile(String url) throws IOException {
        //Загрузка картинки
        //String filePath = "resources" + File.pathSeparator + "filetask";
        String filePath = "./resources/filetask";
        //String filePath = "F:\\atumBot\\src\\main\\resources\\filetask";
        String fileName = "solution";
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

    private boolean checkSubject(String msgUser){

        switch (msgUser){
            case "ветвления": return true;

        }
        return false;
    }

}
