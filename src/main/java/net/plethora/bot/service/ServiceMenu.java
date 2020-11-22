package net.plethora.bot.service;

import java.util.List;


public interface ServiceMenu<T> {

    List<T> start(long chatId, String msgUser);

}
