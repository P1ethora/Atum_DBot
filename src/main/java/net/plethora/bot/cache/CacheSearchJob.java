package net.plethora.bot.cache;

import lombok.Getter;
import lombok.Setter;
import net.plethora.bot.botapi.parsers.InfoForSearch;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
@Setter
public class CacheSearchJob {

    private Map<Long, InfoForSearch> infos = new HashMap<>();

}
