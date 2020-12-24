package net.plethora.bot.service;

import net.plethora.bot.model.UserTelegram;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.List;


public interface ServiceMenu<T> {

    List<T> start(long chatId, String msgUser, UserTelegram userTelegram, int messageId, String inlineMessageId) throws TelegramApiRequestException;
}
