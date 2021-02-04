package com.haiyang.light.console;

import javax.servlet.http.HttpServletRequest;

import org.haiyiyang.server.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.haiyiyang.light.service.proxy.LightServiceFactory;

@RestController
public class IndexController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(HttpServletRequest req) {
		ModelAndView view = new ModelAndView("/WEB-INF/views/index.html");
		view.addObject("total", 1005);
		view.addObject("test", 100);
		UserService userService = LightServiceFactory.getService(UserService.class);
		String helloResult = userService.hello("Agent");
		System.out.println("helloResult===============:" + helloResult);
		view.addObject("helloResult", helloResult);
		return view;
	}

}
