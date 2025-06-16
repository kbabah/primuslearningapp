package com.primuslearning.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Main controller for the application
 */
@Controller
public class HomeController {
    
    /**
     * Home page
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    /**
     * Dashboard page (requires authentication)
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
    
    /**
     * About page
     */
    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
