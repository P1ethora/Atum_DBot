package net.plethora.bot.appconfig;

import net.plethora.bot.botapi.system.CellVacancyMonitoring;
import net.plethora.bot.botapi.system.mailing.MailingPost;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Bean
    public CellVacancyMonitoring cellVacancyMonitoring() {
        return new CellVacancyMonitoring();
    }

    @Bean
    public MailingPost mailing() {
        return new MailingPost();
    }

}
