package net.plethora.bot.controller.webcontroller;

import net.plethora.bot.controlpanel.BaseSessions;
import net.plethora.bot.controlpanel.logic.ConverterToArrayAnswer;
import net.plethora.bot.dao.DataAccessAdmins;
import net.plethora.bot.dao.DataAccessAnswer;
import net.plethora.bot.model.Answer;
import net.plethora.bot.model.UserControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class ControlPanelController {

    @Autowired
    private DataAccessAdmins dataAccessAdmins;
    @Autowired
    private BaseSessions baseSessions;

    @GetMapping("/login")
    public String home(Model model) {
        return "login";
    }

    @PostMapping(value = "login")
    public String login(String user, String pass, Model model, HttpSession session, HttpServletResponse httpServletResponse) {
        UserControl userControl = dataAccessAdmins.findByLoginAndPassword(user, pass);
        if (userControl != null) {
            session.setAttribute("username", userControl.getLogin());
            session.setAttribute("password", userControl.getPassword());
            baseSessions.getSessions().put(session.getId(), session);

            Cookie cookie = new Cookie("user", session.getId());
            httpServletResponse.addCookie(cookie);
            return "main";
        }
        model.addAttribute("error", "invalid username or password");
        return "login";
    }

    @GetMapping("/")
    public String main(HttpServletRequest request, Model model) {
        Cookie[] cookies = request.getCookies();
        String cookieName = "user";
        Cookie cookie = null;
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (cookieName.equals(c.getName())) {
                    cookie = c;
                    break;
                }
            }
        }
        HttpSession session = baseSessions.getSessions().get(cookie.getValue());
        if (session != null) {
            model.addAttribute("username", session.getAttribute("username"));
            return "main";
        }

        return "login";
    }

    @GetMapping("/answer")
    public String answer(HttpServletRequest request, Model model) {

        Cookie[] cookies = request.getCookies();
        String cookieName = "user";
        Cookie cookie = null;
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (cookieName.equals(c.getName())) {
                    cookie = c;
                    break;
                }
            }
        }
        HttpSession session;
        try {
            session = baseSessions.getSessions().get(cookie.getValue());
        } catch (IllegalStateException e) {
            return "login";
        }
        if (session != null) {
            model.addAttribute("username", session.getAttribute("username"));
            return "answer";
        }

        return "redirect:/login";
    }

    @GetMapping("/book")
    public String book() {
        return "book";
    }

}