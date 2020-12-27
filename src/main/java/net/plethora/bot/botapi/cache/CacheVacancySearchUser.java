package net.plethora.bot.botapi.cache;

import lombok.Getter;
import lombok.Setter;
import net.plethora.bot.model.systemmodel.SaveVacancyCell;
import org.springframework.stereotype.Component;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Getter
@Setter
@Component
public class CacheVacancySearchUser {

    private BlockingQueue<SaveVacancyCell> cache = new ArrayBlockingQueue<>(1000000);

}