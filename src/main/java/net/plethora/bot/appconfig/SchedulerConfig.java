package net.plethora.bot.appconfig;

import net.plethora.bot.botapi.system.CellVacancyMonitoring;
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

}
