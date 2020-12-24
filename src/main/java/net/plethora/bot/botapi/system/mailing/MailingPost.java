package net.plethora.bot.botapi.system.mailing;

import net.plethora.bot.AtumBot;
import net.plethora.bot.dao.DataAccessUser;
import net.plethora.bot.model.UserTelegram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MailingPost<T> implements Runnable {

    private final long FOUR_HOURS = 4 * 3600 * 1000;

    @Autowired
    private AtumBot atumBot;
    @Autowired
    private DataAccessUser dataAccessUser;

    @Scheduled(fixedDelay = FOUR_HOURS) //прилетает раз в 4 часа
    @Override
    public void run() {
        System.out.println("Mailing post start");
        for (UserTelegram userTelegram : dataAccessUser.findAll()) {
            long timeSending = userTelegram.getDate().getTime() + FOUR_HOURS;
            Date date = new Date();
            if (timeSending <= date.getTime()) {
                SendMessage sendMessage = new SendMessage();
//TODO добавить базу с записями или парсить группы вк
                sendMessage.setChatId(userTelegram.getIdChat());
                sendMessage.setText("Это просто спам");

                List<T> list = new ArrayList<>();

                list.add((T) sendMessage);
                atumBot.mailing(list);
            }
        }
        System.out.println("Mailing post end");
    }
}