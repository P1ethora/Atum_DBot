package net.plethora.bot.controller;

import net.plethora.bot.dao.DataAccessAdmins;
import net.plethora.bot.dao.repo.PostRepositoryAdmins;
import net.plethora.bot.model.UserControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ControlPanelController {

    @Autowired
    DataAccessAdmins dataAccessAdmins;

    @GetMapping("/login")
    public String home() {
        return "logIn";
    }

    @PostMapping(value = "login")
    public String login(@RequestParam String user, String pass, Model model) {
        UserControl userControl = dataAccessAdmins.findByLoginAndPassword(user, pass);
        if (userControl != null) {
            return "main";
        }

        model.addAttribute("param.error", "doesn't exist");
//        if (error != null) {
//            model.addAttribute("error", "Username or password is incorrect.");
//        }
//
//        if (logout != null) {
//            model.addAttribute("message", "Logged out successfully.");
//        }

        return "redirect:login";
    }
//
//    @RequestMapping(value = {"/", "/control-panel"}, method = RequestMethod.GET)
//    public String controlPanel(Model model) {
//        return "main";
//    }

}