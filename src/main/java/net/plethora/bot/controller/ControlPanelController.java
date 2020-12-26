package net.plethora.bot.controller;

import net.plethora.bot.dao.DataAccessAdmins;
import net.plethora.bot.model.UserControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ControlPanelController {

    @Autowired
    private DataAccessAdmins dataAccessAdmins;

    @GetMapping("/login")
    public String home(Model model) {
        return "login";
    }

    @PostMapping(value = "login")
    public String login(String user, String pass, Model model) {
        UserControl userControl = dataAccessAdmins.findByLoginAndPassword(user, pass);
        if (userControl != null) {
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