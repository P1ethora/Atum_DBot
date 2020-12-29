package net.plethora.bot.controller;

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
    @Autowired
    private DataAccessAnswer dataAccessAnswer;
    @Autowired
    private ConverterToArrayAnswer converter;

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

    @PostMapping("/answer/get")
    public String getAnswer(String ask, Model model) {
        Answer answer = dataAccessAnswer.handleRequest(ask.toLowerCase());
        if (answer == null) {

        } else {
            StringBuilder stringBuilderAsk = new StringBuilder();
            int count = 0;
            for (String ask1 : answer.getAsk()) {
                count++;
                stringBuilderAsk.append(ask1);
                if (count != answer.getAsk().length) {
                    stringBuilderAsk.append(",");
                }
            }

            StringBuilder stringBuilderKey = new StringBuilder();
            count = 0;
            for (String key : answer.getKeyWords()) {
                count++;
                stringBuilderKey.append(key);
                if (count != answer.getKeyWords().length) {
                    stringBuilderKey.append(",");
                }
            }

            model.addAttribute("answer", answer.getAnswer());
            model.addAttribute("asks", stringBuilderAsk);
            model.addAttribute("keys", stringBuilderKey);
            model.addAttribute("id", answer.getId());
        }

        return "answer";
    }

    @PostMapping("answer/get/update")
    public String updateAnswer(@RequestParam String idO, String asks, String keys, String answer) {
        Answer answerOld = dataAccessAnswer.findById(idO);
        System.out.println(answerOld.getAnswer());
        answerOld.setAsk(converter.convert(asks));
        answerOld.setKeyWords(converter.convert(keys));
        answerOld.setAnswer(answer);
        dataAccessAnswer.save(answerOld);

        return "answer";
    }

//    @GetMapping("answer/get/update/{id}")
//    public String getMap(@PathVariable(value = "id") String id){
//
//        return "answer";
//    }

}