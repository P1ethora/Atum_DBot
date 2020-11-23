package net.plethora.bot.service;

import net.plethora.bot.model.User;

import java.util.List;


public interface ServiceMenu<T> {

    List<T> start(long chatId, String msgUser, User user);

}
