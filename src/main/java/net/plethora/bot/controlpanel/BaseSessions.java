package net.plethora.bot.controlpanel;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Component
public class BaseSessions {

    private Map<String, HttpSession> sessions = new HashMap<>();

}