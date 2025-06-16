package com.primuslearning.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Learning Path entity representing educational courses/paths
 */
@Entity
@Table(name = "learning_paths")
public class LearningPath {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    @Column(nullable = false)
    private String title;
    
    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    @Column(nullable = false, length = 1000)
    private String description;
    
    @NotNull(message = "Estimated duration is required")
    @Positive(message = "Duration must be positive")
    @Column(name = "estimated_duration_hours", nullable = false)
    private Integer estimatedDurationHours;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty = Difficulty.BEGINNER;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.DRAFT;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;
    
    @ManyToMany(mappedBy = "enrolledLearningPaths", fetch = FetchType.LAZY)
    private Set<User> enrolledUsers = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "learning_path_tags", joinColumns = @JoinColumn(name = "learning_path_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();
    
    public enum Difficulty {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
    
    public enum Status {
        DRAFT, PUBLISHED, ARCHIVED
    }
    
    // Constructors
    public LearningPath() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public LearningPath(String title, String description, Integer estimatedDurationHours, User creator) {
        this();
        this.title = title;
        this.description = description;
        this.estimatedDurationHours = estimatedDurationHours;
        this.creator = creator;
    }
    
    // Lifecycle methods
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
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
    
    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }
    
    public Set<User> getEnrolledUsers() { return enrolledUsers; }
    public void setEnrolledUsers(Set<User> enrolledUsers) { this.enrolledUsers = enrolledUsers; }
    
    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }
    
    // Helper methods
    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty()) {
            this.tags.add(tag.trim().toLowerCase());
        }
    }
    
    public void removeTag(String tag) {
        if (tag != null) {
            this.tags.remove(tag.trim().toLowerCase());
        }
    }
    
    public int getEnrollmentCount() {
        return enrolledUsers.size();
    }
    
    public String getFormattedDuration() {
        if (estimatedDurationHours == null) return "Unknown";
        if (estimatedDurationHours == 1) return "1 hour";
        return estimatedDurationHours + " hours";
    }
    
    public String getTagsAsString() {
        return String.join(", ", tags);
    }
    
    public void setTagsFromString(String tagsString) {
        this.tags.clear();
        if (tagsString != null && !tagsString.trim().isEmpty()) {
            String[] tagArray = tagsString.split(",");
            for (String tag : tagArray) {
                addTag(tag);
            }
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LearningPath)) return false;
        LearningPath that = (LearningPath) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "LearningPath{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", difficulty=" + difficulty +
                ", status=" + status +
                '}';
    }
}
