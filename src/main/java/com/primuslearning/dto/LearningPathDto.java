package com.primuslearning.dto;

import com.primuslearning.entity.LearningPath;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for Learning Path operations
 */
public class LearningPathDto {
    
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;
    
    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;
    
    @NotNull(message = "Estimated duration is required")
    @Positive(message = "Duration must be positive")
    private Integer estimatedDurationHours;
    
    private LearningPath.Difficulty difficulty = LearningPath.Difficulty.BEGINNER;
    
    private LearningPath.Status status = LearningPath.Status.DRAFT;
    
    private String tags;
    
    // Constructors
    public LearningPathDto() {}
    
    public LearningPathDto(String title, String description, Integer estimatedDurationHours) {
        this.title = title;
        this.description = description;
        this.estimatedDurationHours = estimatedDurationHours;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getEstimatedDurationHours() { return estimatedDurationHours; }
    public void setEstimatedDurationHours(Integer estimatedDurationHours) { this.estimatedDurationHours = estimatedDurationHours; }
    
    public LearningPath.Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(LearningPath.Difficulty difficulty) { this.difficulty = difficulty; }
    
    public LearningPath.Status getStatus() { return status; }
    public void setStatus(LearningPath.Status status) { this.status = status; }
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
}
