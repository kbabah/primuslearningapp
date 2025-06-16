package com.primuslearning.controller;

import com.primuslearning.entity.LearningPath;
import com.primuslearning.entity.User;
import com.primuslearning.service.LearningPathService;
import com.primuslearning.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Admin controller for administrative operations
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final UserService userService;
    private final LearningPathService learningPathService;
    
    @Autowired
    public AdminController(UserService userService, LearningPathService learningPathService) {
        this.userService = userService;
        this.learningPathService = learningPathService;
    }
    
    /**
     * Admin dashboard
     */
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        List<User> allUsers = userService.getAllUsers();
        
        // Statistics
        long totalUsers = allUsers.size();
        long adminCount = allUsers.stream().filter(u -> u.getRole() == User.Role.ADMIN).count();
        long userCount = allUsers.stream().filter(u -> u.getRole() == User.Role.USER).count();
        
        Page<LearningPath> allLearningPaths = learningPathService.getAllLearningPaths(
            PageRequest.of(0, Integer.MAX_VALUE));
        long totalLearningPaths = allLearningPaths.getTotalElements();
        long publishedPaths = allLearningPaths.getContent().stream()
            .filter(lp -> lp.getStatus() == LearningPath.Status.PUBLISHED).count();
        long draftPaths = allLearningPaths.getContent().stream()
            .filter(lp -> lp.getStatus() == LearningPath.Status.DRAFT).count();
        
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("adminCount", adminCount);
        model.addAttribute("userCount", userCount);
        model.addAttribute("totalLearningPaths", totalLearningPaths);
        model.addAttribute("publishedPaths", publishedPaths);
        model.addAttribute("draftPaths", draftPaths);
        
        // Recent users and learning paths
        List<User> recentUsers = userService.getAllUsers().stream()
            .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
            .limit(5)
            .toList();
        
        Page<LearningPath> recentLearningPaths = learningPathService.getAllLearningPaths(
            PageRequest.of(0, 5, Sort.by("createdAt").descending()));
        
        model.addAttribute("recentUsers", recentUsers);
        model.addAttribute("recentLearningPaths", recentLearningPaths.getContent());
        
        return "admin/dashboard";
    }
    
    /**
     * Manage users
     */
    @GetMapping("/users")
    public String manageUsers(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "20") int size,
                              Model model) {
        List<User> allUsers = userService.getAllUsers();
        
        // Manual pagination
        int start = page * size;
        int end = Math.min(start + size, allUsers.size());
        List<User> users = allUsers.subList(start, end);
        
        int totalPages = (int) Math.ceil((double) allUsers.size() / size);
        
        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalElements", allUsers.size());
        
        return "admin/users";
    }
    
    /**
     * Manage learning paths
     */
    @GetMapping("/learning-paths")
    public String manageLearningPaths(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size,
                                      @RequestParam(required = false) String status,
                                      Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        Page<LearningPath> learningPaths;
        if (status != null && !status.isEmpty()) {
            LearningPath.Status statusEnum = LearningPath.Status.valueOf(status);
            learningPaths = learningPathService.getLearningPathsByStatus(statusEnum, pageable);
            model.addAttribute("selectedStatus", status);
        } else {
            learningPaths = learningPathService.getAllLearningPaths(pageable);
        }
        
        model.addAttribute("learningPaths", learningPaths);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", learningPaths.getTotalPages());
        model.addAttribute("statuses", LearningPath.Status.values());
        
        return "admin/learning-paths";
    }
    
    /**
     * Toggle user enabled status
     */
    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
            userService.setUserEnabled(id, !user.isEnabled());
            redirectAttributes.addFlashAttribute("success", 
                "User " + (user.isEnabled() ? "disabled" : "enabled") + " successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update user status: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
    
    /**
     * Promote user to admin
     */
    @PostMapping("/users/{id}/promote")
    public String promoteToAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.promoteToAdmin(id);
            redirectAttributes.addFlashAttribute("success", "User promoted to admin successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to promote user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
    
    /**
     * Delete user
     */
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
    
    /**
     * Publish learning path
     */
    @PostMapping("/learning-paths/{id}/publish")
    public String publishLearningPath(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            learningPathService.publishLearningPath(id);
            redirectAttributes.addFlashAttribute("success", "Learning path published successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to publish learning path: " + e.getMessage());
        }
        return "redirect:/admin/learning-paths";
    }
    
    /**
     * Archive learning path
     */
    @PostMapping("/learning-paths/{id}/archive")
    public String archiveLearningPath(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            learningPathService.archiveLearningPath(id);
            redirectAttributes.addFlashAttribute("success", "Learning path archived successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to archive learning path: " + e.getMessage());
        }
        return "redirect:/admin/learning-paths";
    }
    
    /**
     * Delete learning path (admin only)
     */
    @PostMapping("/learning-paths/{id}/delete")
    public String deleteLearningPath(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            learningPathService.deleteLearningPath(id);
            redirectAttributes.addFlashAttribute("success", "Learning path deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete learning path: " + e.getMessage());
        }
        return "redirect:/admin/learning-paths";
    }
}
