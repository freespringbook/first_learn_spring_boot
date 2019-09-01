package com.web.controller;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by freejava1191@gmail.com on 2019-09-01
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
public class HomeController {

    @GetMapping("")
    public String home() {
        return "home";
    }
}
