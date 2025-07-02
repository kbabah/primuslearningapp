package com.primuslearning.config;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Date;

/**
 * Global exception handler for the application
 * Provides centralized exception handling and prevents leaking sensitive information
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final Environment environment;

    public GlobalExceptionHandler(Environment environment) {
        this.environment = environment;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model) {
        logger.warn("Access denied: {}", ex.getMessage());
        model.addAttribute("errorMessage", "You don't have permission to access this resource");
        return "error/403";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleAllExceptions(HttpServletRequest request, Exception ex) {
        logger.error("Unhandled exception occurred", ex);
        
        ModelAndView modelAndView = new ModelAndView("error/error");
        modelAndView.addObject("timestamp", new Date());
        modelAndView.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        modelAndView.addObject("path", request.getRequestURI());
        
        // Only show detailed error information in development environment
        boolean isDevProfile = Arrays.asList(environment.getActiveProfiles()).contains("dev");
        if (isDevProfile) {
            modelAndView.addObject("exception", ex.getClass().getName());
            modelAndView.addObject("message", ex.getMessage());
            modelAndView.addObject("trace", ex.getStackTrace());
        } else {
            modelAndView.addObject("message", "An unexpected error occurred. Please contact support.");
        }
        
        return modelAndView;
    }
}