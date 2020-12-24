package net.plethora.bot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ControlPanelController {

    @GetMapping("/")
    public String home(@RequestParam(name = "title", required = false, defaultValue = "Omega") String name, Model model) {
        model.addAttribute("title", "MAIN PAGE");
        return "home";
    }

}