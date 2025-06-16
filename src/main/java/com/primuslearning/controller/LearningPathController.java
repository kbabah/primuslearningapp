package com.primuslearning.controller;

import com.primuslearning.dto.LearningPathDto;
import com.primuslearning.entity.LearningPath;
import com.primuslearning.entity.User;
import com.primuslearning.service.LearningPathService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Controller for Learning Path operations
 */
@Controller
@RequestMapping("/learning-paths")
public class LearningPathController {
    
    private final LearningPathService learningPathService;
    
    @Autowired
    public LearningPathController(LearningPathService learningPathService) {
        this.learningPathService = learningPathService;
    }
    
    /**
     * List all published learning paths with pagination and search
     */
    @GetMapping
    public String listLearningPaths(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String tag,
            Model model) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<LearningPath> learningPaths;
        
        if (search != null && !search.trim().isEmpty()) {
            learningPaths = learningPathService.searchLearningPaths(search, pageable);
            model.addAttribute("search", search);
        } else if (difficulty != null && !difficulty.isEmpty()) {
            LearningPath.Difficulty diff = LearningPath.Difficulty.valueOf(difficulty);
            learningPaths = learningPathService.getLearningPathsByDifficulty(diff, pageable);
            model.addAttribute("selectedDifficulty", difficulty);
        } else if (tag != null && !tag.isEmpty()) {
            learningPaths = learningPathService.getLearningPathsByTag(tag, pageable);
            model.addAttribute("selectedTag", tag);
        } else {
            learningPaths = learningPathService.getPublishedLearningPaths(pageable);
        }
        
        model.addAttribute("learningPaths", learningPaths);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", learningPaths.getTotalPages());
        model.addAttribute("totalElements", learningPaths.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("availableTags", learningPathService.getAllTags());
        model.addAttribute("difficulties", LearningPath.Difficulty.values());
        
        return "learning-path/list";
    }
    
    /**
     * Show learning path details
     */
    @GetMapping("/{id}")
    public String showLearningPath(@PathVariable Long id, 
                                   @AuthenticationPrincipal User currentUser,
                                   Model model) {
        Optional<LearningPath> learningPathOpt = learningPathService.findById(id);
        if (learningPathOpt.isEmpty()) {
            return "redirect:/learning-paths?error=notfound";
        }
        
        LearningPath learningPath = learningPathOpt.get();
        model.addAttribute("learningPath", learningPath);
        
        if (currentUser != null) {
            boolean isEnrolled = learningPathService.isUserEnrolled(id, currentUser);
            model.addAttribute("isEnrolled", isEnrolled);
            model.addAttribute("isOwner", learningPath.getCreator().getId().equals(currentUser.getId()));
        }
        
        return "learning-path/detail";
    }
    
    /**
     * Show create learning path form
     */
    @GetMapping("/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("learningPathDto", new LearningPathDto());
        model.addAttribute("difficulties", LearningPath.Difficulty.values());
        model.addAttribute("statuses", LearningPath.Status.values());
        return "learning-path/create";
    }
    
    /**
     * Process create learning path
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String createLearningPath(@Valid @ModelAttribute LearningPathDto learningPathDto,
                                     BindingResult result,
                                     @AuthenticationPrincipal User currentUser,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("difficulties", LearningPath.Difficulty.values());
            model.addAttribute("statuses", LearningPath.Status.values());
            return "learning-path/create";
        }
        
        try {
            LearningPath learningPath = new LearningPath();
            learningPath.setTitle(learningPathDto.getTitle());
            learningPath.setDescription(learningPathDto.getDescription());
            learningPath.setEstimatedDurationHours(learningPathDto.getEstimatedDurationHours());
            learningPath.setDifficulty(learningPathDto.getDifficulty());
            learningPath.setStatus(learningPathDto.getStatus());
            learningPath.setCreator(currentUser);
            
            if (learningPathDto.getTags() != null) {
                learningPath.setTagsFromString(learningPathDto.getTags());
            }
            
            LearningPath savedLearningPath = learningPathService.createLearningPath(learningPath);
            
            redirectAttributes.addFlashAttribute("success", "Learning path created successfully!");
            return "redirect:/learning-paths/" + savedLearningPath.getId();
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create learning path: " + e.getMessage());
            model.addAttribute("difficulties", LearningPath.Difficulty.values());
            model.addAttribute("statuses", LearningPath.Status.values());
            return "learning-path/create";
        }
    }
    
    /**
     * Show edit learning path form
     */
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @learningPathService.findById(#id).orElse(null)?.creator?.id == authentication.principal.id)")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<LearningPath> learningPathOpt = learningPathService.findById(id);
        if (learningPathOpt.isEmpty()) {
            return "redirect:/learning-paths?error=notfound";
        }
        
        LearningPath learningPath = learningPathOpt.get();
        LearningPathDto dto = new LearningPathDto();
        dto.setId(learningPath.getId());
        dto.setTitle(learningPath.getTitle());
        dto.setDescription(learningPath.getDescription());
        dto.setEstimatedDurationHours(learningPath.getEstimatedDurationHours());
        dto.setDifficulty(learningPath.getDifficulty());
        dto.setStatus(learningPath.getStatus());
        dto.setTags(learningPath.getTagsAsString());
        
        model.addAttribute("learningPathDto", dto);
        model.addAttribute("difficulties", LearningPath.Difficulty.values());
        model.addAttribute("statuses", LearningPath.Status.values());
        
        return "learning-path/edit";
    }
    
    /**
     * Process edit learning path
     */
    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @learningPathService.findById(#id).orElse(null)?.creator?.id == authentication.principal.id)")
    public String editLearningPath(@PathVariable Long id,
                                   @Valid @ModelAttribute LearningPathDto learningPathDto,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            learningPathDto.setId(id);
            model.addAttribute("difficulties", LearningPath.Difficulty.values());
            model.addAttribute("statuses", LearningPath.Status.values());
            return "learning-path/edit";
        }
        
        try {
            Optional<LearningPath> learningPathOpt = learningPathService.findById(id);
            if (learningPathOpt.isEmpty()) {
                return "redirect:/learning-paths?error=notfound";
            }
            
            LearningPath learningPath = learningPathOpt.get();
            learningPath.setTitle(learningPathDto.getTitle());
            learningPath.setDescription(learningPathDto.getDescription());
            learningPath.setEstimatedDurationHours(learningPathDto.getEstimatedDurationHours());
            learningPath.setDifficulty(learningPathDto.getDifficulty());
            learningPath.setStatus(learningPathDto.getStatus());
            
            if (learningPathDto.getTags() != null) {
                learningPath.setTagsFromString(learningPathDto.getTags());
            }
            
            learningPathService.updateLearningPath(learningPath);
            
            redirectAttributes.addFlashAttribute("success", "Learning path updated successfully!");
            return "redirect:/learning-paths/" + id;
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update learning path: " + e.getMessage());
            learningPathDto.setId(id);
            model.addAttribute("difficulties", LearningPath.Difficulty.values());
            model.addAttribute("statuses", LearningPath.Status.values());
            return "learning-path/edit";
        }
    }
    
    /**
     * Delete learning path
     */
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @learningPathService.findById(#id).orElse(null)?.creator?.id == authentication.principal.id)")
    public String deleteLearningPath(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            learningPathService.deleteLearningPath(id);
            redirectAttributes.addFlashAttribute("success", "Learning path deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete learning path: " + e.getMessage());
        }
        return "redirect:/learning-paths";
    }
    
    /**
     * Enroll in learning path
     */
    @PostMapping("/enroll/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String enrollInLearningPath(@PathVariable Long id,
                                       @AuthenticationPrincipal User currentUser,
                                       RedirectAttributes redirectAttributes) {
        try {
            learningPathService.enrollUser(id, currentUser);
            redirectAttributes.addFlashAttribute("success", "Successfully enrolled in learning path!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to enroll: " + e.getMessage());
        }
        return "redirect:/learning-paths/" + id;
    }
    
    /**
     * Unenroll from learning path
     */
    @PostMapping("/unenroll/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String unenrollFromLearningPath(@PathVariable Long id,
                                           @AuthenticationPrincipal User currentUser,
                                           RedirectAttributes redirectAttributes) {
        try {
            learningPathService.unenrollUser(id, currentUser);
            redirectAttributes.addFlashAttribute("success", "Successfully unenrolled from learning path!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to unenroll: " + e.getMessage());
        }
        return "redirect:/learning-paths/" + id;
    }
    
    /**
     * My learning paths (created by user)
     */
    @GetMapping("/my-paths")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String myLearningPaths(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @AuthenticationPrincipal User currentUser,
                                  Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<LearningPath> learningPaths = learningPathService.getLearningPathsByCreator(currentUser, pageable);
        
        model.addAttribute("learningPaths", learningPaths);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", learningPaths.getTotalPages());
        model.addAttribute("totalElements", learningPaths.getTotalElements());
        
        return "learning-path/my-paths";
    }
    
    /**
     * My enrolled learning paths
     */
    @GetMapping("/my-enrollments")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String myEnrollments(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @AuthenticationPrincipal User currentUser,
                                Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<LearningPath> learningPaths = learningPathService.getEnrolledLearningPaths(currentUser.getId(), pageable);
        
        model.addAttribute("learningPaths", learningPaths);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", learningPaths.getTotalPages());
        
        return "learning-path/my-enrollments";
    }
}
