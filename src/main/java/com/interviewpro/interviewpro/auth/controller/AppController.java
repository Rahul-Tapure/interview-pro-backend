package com.interviewpro.interviewpro.auth.controller;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class AppController {
	@RequestMapping({"/", "/{path:^(?!interviewpro).*}/**"})
	public String forward() {
	    return "forward:/index.html";
	}

}
