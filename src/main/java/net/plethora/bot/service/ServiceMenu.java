package net.plethora.bot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


public interface ServiceMenu {

    SendMessage start(long chatId,String msgUser);

}
