package org.paccounts.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping(value = {"/"})
    public String home() {
        return "home";
    }

//    @GetMapping(value = {"/home", "/index"})
//    public String index() {
//        return "redirect:/";
//    }
}
