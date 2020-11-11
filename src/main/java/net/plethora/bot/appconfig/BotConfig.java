package net.plethora.bot.appconfig;

import lombok.Getter;
import lombok.Setter;
import net.plethora.bot.AtumBot;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "telegrambot")
public class BotConfig {

    private String botPath;
    private String botUsername;
    private String botToken;

    private DefaultBotOptions.ProxyType proxyType;
    private String proxyHost;
    private int proxyPort;

    @Bean
    public AtumBot atumBot() {
        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);


        botOptions.setProxyType(proxyType);
        botOptions.setProxyHost(proxyHost);
        botOptions.setProxyPort(proxyPort);

        AtumBot atumBot = new AtumBot(botOptions);
        atumBot.setBotPath(botPath);
        atumBot.setBotUsername(botUsername);
        atumBot.setBotToken(botToken);

        return atumBot;
    }
}