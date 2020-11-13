package net.plethora.bot.botapi;


import net.plethora.bot.cache.CacheUsersState;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class Menu {
    //TODO сделать меню
    private ProcessingStates processingStates;
    private CacheUsersState cacheUsersState;

    public Menu(ProcessingStates processingStates, CacheUsersState cacheUsersState) {
        this.processingStates = processingStates;
        this.cacheUsersState = cacheUsersState;
    }

    public String start(Update update) {
        long idChat = update.getMessage().getChatId();
        String enteredCommand = update.getMessage().getText();


        return "Вы вошли в меню";
    }
}