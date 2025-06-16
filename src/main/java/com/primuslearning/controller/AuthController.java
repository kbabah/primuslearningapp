package com.primuslearning.controller;

import com.primuslearning.dto.UserRegistrationDto;
import com.primuslearning.entity.User;
import com.primuslearning.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for user authentication operations
 */
@Controller
public class AuthController {
    
    private final UserService userService;
    
    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Show login form
     */
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        return "auth/login";
    }
    
    /**
     * Show registration form
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userRegistration", new UserRegistrationDto());
        return "auth/register";
    }
    
    /**
     * Process user registration
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userRegistration") UserRegistrationDto registrationDto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        
        // Check for validation errors
        if (result.hasErrors()) {
            return "auth/register";
        }
        
        // Check if passwords match
        if (!registrationDto.isPasswordMatching()) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
            return "auth/register";
        }
        
        try {
            // Check if username already exists
            if (userService.existsByUsername(registrationDto.getUsername())) {
                result.rejectValue("username", "error.username", "Username already exists");
                return "auth/register";
            }
            
            // Check if email already exists
            if (userService.existsByEmail(registrationDto.getEmail())) {
                result.rejectValue("email", "error.email", "Email already exists");
                return "auth/register";
            }
            
            // Create new user
            User newUser = new User();
            newUser.setUsername(registrationDto.getUsername());
            newUser.setEmail(registrationDto.getEmail());
            newUser.setPassword(registrationDto.getPassword());
            newUser.setFirstName(registrationDto.getFirstName());
            newUser.setLastName(registrationDto.getLastName());
            newUser.setRole(User.Role.USER);
            
            userService.registerUser(newUser);
            
            redirectAttributes.addFlashAttribute("success", 
                "Registration successful! You can now log in with your credentials.");
            return "redirect:/login";
            
        } catch (Exception e) {
            result.rejectValue("username", "error.registration", "Registration failed: " + e.getMessage());
            return "auth/register";
        }
    }
}
