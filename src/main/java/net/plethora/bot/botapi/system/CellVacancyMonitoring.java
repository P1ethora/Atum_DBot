package net.plethora.bot.botapi.system;

import lombok.SneakyThrows;
import net.plethora.bot.cache.CacheVacancySearchUser;
import net.plethora.bot.model.systemmodel.SaveVacancyCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.Iterator;


public class CellVacancyMonitoring {

    @Autowired
    private CacheVacancySearchUser cacheVacancySearchUser;

    @SneakyThrows
    @Scheduled(fixedDelay = 3600 * 1000)
    public void run() {
        System.out.println("Monitoring cell vacancy start");
        if (cacheVacancySearchUser.getCache().size() > 0) {
            Iterator<SaveVacancyCell> it = cacheVacancySearchUser.getCache().iterator();
            while (it.hasNext()) {
                Date date = new Date();
                if (it.next().getDateDelete().before(date)) {
                    it.remove();
                }
            }
        }
        System.out.println("Monitoring cell vacancy end");
    }
}