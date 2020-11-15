package net.plethora.bot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class PhrasesService {

    private final Locale locale;
    private final MessageSource messageSource;

    public PhrasesService(@Value("${localeTag}") String localeTag, MessageSource messageSource) {
        this.messageSource = messageSource;
        this.locale = Locale.forLanguageTag(localeTag);
    }
    
    public String getMessage(String message) {
        return messageSource.getMessage(message, null, locale);
    }
}