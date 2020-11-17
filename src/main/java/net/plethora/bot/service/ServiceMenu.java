package net.plethora.bot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;


public interface ServiceMenu {

    List<SendMessage> start(long chatId, String msgUser);

}
