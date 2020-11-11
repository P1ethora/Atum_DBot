package net.plethora.bot.botapi;

import org.springframework.stereotype.Component;


import java.util.HashMap;

import java.util.Map;

@Component
public class ListCommand {

    public Map<String, BotState> getListCommand() {
        return listCommand;
    }

    private Map<String,BotState> listCommand = new HashMap<>();

    public ListCommand(){

        listCommand.put("/start",BotState.START);

    }

}
