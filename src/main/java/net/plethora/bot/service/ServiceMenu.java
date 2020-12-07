package net.plethora.bot.service;

import net.plethora.bot.model.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.List;


public interface ServiceMenu<T> {

    List<T> start(long chatId, String msgUser, User user,int messageId,String inlineMessageId) throws TelegramApiRequestException;
}
