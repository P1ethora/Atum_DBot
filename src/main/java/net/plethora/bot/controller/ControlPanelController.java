package net.plethora.bot.controller;

import net.plethora.bot.dao.DataAccessAdmins;
import net.plethora.bot.model.UserControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class ControlPanelController {

    @Autowired
    private DataAccessAdmins dataAccessAdmins;

    @GetMapping("/login")
    public String home(Model model) {
        return "login";
    }

    @PostMapping(value = "login")
    public String login(String user, String pass, Model model, HttpSession session) {
        UserControl userControl = dataAccessAdmins.findByLoginAndPassword(user, pass);
        if (userControl != null) {
            session.setAttribute("username", userControl.getLogin());
            session.setAttribute("password", userControl.getPassword());
            return "main";
        }
        model.addAttribute("error", "invalid username or password");
        return "login";
    }

    @GetMapping("/")
    public String main(UserControl userControl, Model model) {
        if ((dataAccessAdmins.findByLoginAndPassword(userControl.getLogin(), userControl.getPassword()) == null)) {
            return "redirect:/login";
        }
        return "main";
    }
}