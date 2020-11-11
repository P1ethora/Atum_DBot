package net.plethora.bot.cache;

import lombok.Getter;
import lombok.Setter;
import net.plethora.bot.botapi.BotState;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Component
public class CacheBotState {

    public Map<Long, BotState> stateUsers = new HashMap<>();

}
