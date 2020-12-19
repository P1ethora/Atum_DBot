package net.plethora.bot.cache;

import lombok.Getter;
import lombok.Setter;
import net.plethora.bot.model.systemmodel.SaveVacancyCell;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Getter
@Setter
@Component
public class CacheVacancySearchUser {

    //private List<SaveVacancyCell> cache = new ArrayList<>();
    private BlockingQueue<SaveVacancyCell> cache = new ArrayBlockingQueue<>(1000000);

}