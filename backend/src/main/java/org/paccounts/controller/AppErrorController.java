package org.paccounts.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.paccounts.component.ModelAndViewPopulator;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppErrorController implements ErrorController {

    ModelAndViewPopulator modelAndViewPopulator;

    public AppErrorController(ModelAndViewPopulator modelAndViewPopulator) {
        this.modelAndViewPopulator = modelAndViewPopulator;
    }

    @GetMapping(value = "/error")
    public ModelAndView handleError(HttpServletRequest request, ModelAndView modelAndView) {
        return modelAndViewPopulator.fillError404(request, modelAndView);
    }
}
