package net.plethora.bot.botapi.cache;

import lombok.Getter;
import lombok.Setter;
import net.plethora.bot.model.systemmodel.InfoForSearch;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Хранилище на время создание объекта InfoForSearch
 * как только объект создан он переводиться в длительное хранилище
 */
@Setter
@Getter
@Component
public class ProvisionalObjectInfo {
    Map<Long, InfoForSearch> infoForSearches = new HashMap<>();
}