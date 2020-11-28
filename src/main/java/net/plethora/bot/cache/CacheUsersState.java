package net.plethora.bot.cache;

import lombok.Getter;
import lombok.Setter;
import net.plethora.bot.botapi.state.BotState;
import net.plethora.bot.botapi.ProcessingStates;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Component
public class CacheUsersState {

    private Map<Long, BotState> stateUsers = new HashMap<>();

    private ProcessingStates processingStates;

    public CacheUsersState(ProcessingStates processingStates) {
        this.processingStates = processingStates;
    }


}
