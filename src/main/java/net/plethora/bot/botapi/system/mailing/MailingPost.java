package net.plethora.bot.botapi.system.mailing;

import net.plethora.bot.AtumBot;
import net.plethora.bot.dao.DataAccessUser;
import net.plethora.bot.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;

public class MailingPost<T> implements Runnable {

    @Autowired
    private AtumBot atumBot;
    @Autowired
    private DataAccessUser dataAccessUser;

    @Scheduled(fixedDelay = 3600 * 1000)
    @Override
    public void run() {
        for (User user : dataAccessUser.findAll()) {
            SendMessage sendMessage = new SendMessage();
//TODO добавить базу с записями или парсить группы вк
            //TODO доавить пользователю время регистрации, и чтобы спам прилетал через 3 часа
            sendMessage.setChatId(user.getIdChat());
            sendMessage.setText("Это просто спам");

            List<T> list = new ArrayList<>();

            list.add((T) sendMessage);
            atumBot.mailing(list);
        }
    }

}