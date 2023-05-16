package ro.licenta.client.frontend.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorHandler implements ErrorController {
	
	@RequestMapping("/error")
    public ModelAndView handleError404(HttpServletRequest request, Exception e)   {
        return new ModelAndView("404");
    }
	
}
