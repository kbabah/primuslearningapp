package com.primuslearning.service;

import com.primuslearning.entity.LearningPath;
import com.primuslearning.entity.User;
import com.primuslearning.repository.LearningPathRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for LearningPath-related operations
 */
@Service
@Transactional
public class LearningPathService {
    
    private final LearningPathRepository learningPathRepository;
    
    @Autowired
    public LearningPathService(LearningPathRepository learningPathRepository) {
        this.learningPathRepository = learningPathRepository;
    }
    
    /**
     * Create a new learning path
     */
    public LearningPath createLearningPath(LearningPath learningPath) {
        return learningPathRepository.save(learningPath);
    }
    
    /**
     * Find learning path by ID
     */
    public Optional<LearningPath> findById(Long id) {
        return learningPathRepository.findById(id);
    }
    
    /**
     * Get all learning paths with pagination
     */
    public Page<LearningPath> getAllLearningPaths(Pageable pageable) {
        return learningPathRepository.findAll(pageable);
    }
    
    /**
     * Get published learning paths
     */
    public Page<LearningPath> getPublishedLearningPaths(Pageable pageable) {
        return learningPathRepository.findPublishedLearningPaths(pageable);
    }
    
    /**
     * Get learning paths by creator
     */
    public Page<LearningPath> getLearningPathsByCreator(User creator, Pageable pageable) {
        return learningPathRepository.findByCreator(creator, pageable);
    }
    
    /**
     * Update learning path
     */
    public LearningPath updateLearningPath(LearningPath learningPath) {
        return learningPathRepository.save(learningPath);
    }
    
    /**
     * Delete learning path
     */
    public void deleteLearningPath(Long id) {
        learningPathRepository.deleteById(id);
    }
    
    /**
     * Search learning paths
     */
    public Page<LearningPath> searchLearningPaths(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getPublishedLearningPaths(pageable);
        }
        return learningPathRepository.searchByTitleOrDescription(searchTerm.trim(), pageable);
    }
    
    /**
     * Find learning paths by difficulty
     */
    public Page<LearningPath> getLearningPathsByDifficulty(LearningPath.Difficulty difficulty, Pageable pageable) {
        return learningPathRepository.findByDifficulty(difficulty, pageable);
    }
    
    /**
     * Find learning paths by status
     */
    public Page<LearningPath> getLearningPathsByStatus(LearningPath.Status status, Pageable pageable) {
        return learningPathRepository.findByStatus(status, pageable);
    }
    
    /**
     * Find learning paths by tag
     */
    public Page<LearningPath> getLearningPathsByTag(String tag, Pageable pageable) {
        return learningPathRepository.findByTag(tag, pageable);
    }
    
    /**
     * Get enrolled learning paths for a user
     */
    public Page<LearningPath> getEnrolledLearningPaths(Long userId, Pageable pageable) {
        return learningPathRepository.findEnrolledLearningPaths(userId, pageable);
    }
    
    /**
     * Advanced search with multiple criteria
     */
    public Page<LearningPath> searchWithCriteria(String searchTerm, 
                                                LearningPath.Difficulty difficulty,
                                                LearningPath.Status status,
                                                Pageable pageable) {
        return learningPathRepository.findWithCriteria(searchTerm, difficulty, status, pageable);
    }
    
    /**
     * Get all available tags
     */
    public List<String> getAllTags() {
        return learningPathRepository.findAllTags();
    }
    
    /**
     * Enroll user in learning path
     */
    public void enrollUser(Long learningPathId, User user) {
        Optional<LearningPath> learningPathOpt = learningPathRepository.findById(learningPathId);
        if (learningPathOpt.isPresent()) {
            LearningPath learningPath = learningPathOpt.get();
            if (learningPath.getStatus() == LearningPath.Status.PUBLISHED) {
                user.enrollInLearningPath(learningPath);
                learningPathRepository.save(learningPath);
            } else {
                throw new IllegalStateException("Cannot enroll in unpublished learning path");
            }
        } else {
            throw new IllegalArgumentException("Learning path not found");
        }
    }
    
    /**
     * Unenroll user from learning path
     */
    public void unenrollUser(Long learningPathId, User user) {
        Optional<LearningPath> learningPathOpt = learningPathRepository.findById(learningPathId);
        if (learningPathOpt.isPresent()) {
            LearningPath learningPath = learningPathOpt.get();
            learningPath.getEnrolledUsers().remove(user);
            user.getEnrolledLearningPaths().remove(learningPath);
            learningPathRepository.save(learningPath);
        }
    }
    
    /**
     * Check if user is enrolled in learning path
     */
    public boolean isUserEnrolled(Long learningPathId, User user) {
        Optional<LearningPath> learningPathOpt = learningPathRepository.findById(learningPathId);
        if (learningPathOpt.isPresent()) {
            return learningPathOpt.get().getEnrolledUsers().contains(user);
        }
        return false;
    }
    
    /**
     * Publish learning path
     */
    public void publishLearningPath(Long id) {
        Optional<LearningPath> learningPathOpt = learningPathRepository.findById(id);
        if (learningPathOpt.isPresent()) {
            LearningPath learningPath = learningPathOpt.get();
            learningPath.setStatus(LearningPath.Status.PUBLISHED);
            learningPathRepository.save(learningPath);
        }
    }
    
    /**
     * Archive learning path
     */
    public void archiveLearningPath(Long id) {
        Optional<LearningPath> learningPathOpt = learningPathRepository.findById(id);
        if (learningPathOpt.isPresent()) {
            LearningPath learningPath = learningPathOpt.get();
            learningPath.setStatus(LearningPath.Status.ARCHIVED);
            learningPathRepository.save(learningPath);
        }
    }
}
