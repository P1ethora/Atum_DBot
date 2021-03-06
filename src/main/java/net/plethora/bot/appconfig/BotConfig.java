package net.plethora.bot.appconfig;

import lombok.Getter;
import lombok.Setter;
import net.plethora.bot.AtumBot;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        ExecutorService executorService = Executors.newCachedThreadPool();
//        botOptions.setProxyType(proxyType);
//        botOptions.setProxyHost(proxyHost);
//        botOptions.setProxyPort(proxyPort);

        AtumBot atumBot = new AtumBot(botOptions, executorService);
        atumBot.setBotPath(botPath);
        atumBot.setBotUsername(botUsername);
        atumBot.setBotToken(botToken);

        return atumBot;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:botPhrases");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}