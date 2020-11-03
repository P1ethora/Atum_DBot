package net.plethora.atumBot.controller;

import com.google.inject.internal.cglib.reflect.$FastMethod;
import net.plethora.atumBot.AtumBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class WebController {
    private final AtumBot atumBot;

    @Autowired
    public WebController(AtumBot atumBot) {
        this.atumBot = atumBot;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update){
        return atumBot.onWebhookUpdateReceived(update);
    }

}
