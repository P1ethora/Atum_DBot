package net.plethora.bot.cache;

import lombok.Getter;
import lombok.Setter;
import net.plethora.bot.model.systemmodel.SaveVacancyCell;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
public class CacheVacancySearchUser {

    private List<SaveVacancyCell> cache = new ArrayList<>();

}