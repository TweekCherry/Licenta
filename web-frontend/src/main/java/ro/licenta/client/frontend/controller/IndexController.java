package ro.licenta.client.frontend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

	@GetMapping("/{path:^[^.]*$}")
	public ModelAndView getDevelopment(@PathVariable String path) {
		ModelAndView modelAndView = new ModelAndView("index", HttpStatus.OK);
		return modelAndView;
	}
	
}
