package com.haiyang.light.console.config.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
public class ConfigController {

	@GetMapping("/name/{name}")
	public String showName(@PathVariable String name) {
		return "Your name is " + name;
	}

}
