package com.primuslearning.repository;

import com.primuslearning.entity.LearningPath;
import com.primuslearning.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for LearningPath entity
 */
@Repository
public interface LearningPathRepository extends JpaRepository<LearningPath, Long> {
    
    /**
     * Find learning paths by creator
     */
    Page<LearningPath> findByCreator(User creator, Pageable pageable);
    
    /**
     * Find learning paths by status
     */
    Page<LearningPath> findByStatus(LearningPath.Status status, Pageable pageable);
    
    /**
     * Find learning paths by difficulty
     */
    Page<LearningPath> findByDifficulty(LearningPath.Difficulty difficulty, Pageable pageable);
    
    /**
     * Search learning paths by title or description
     */
    @Query("SELECT lp FROM LearningPath lp WHERE " +
           "LOWER(lp.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(lp.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<LearningPath> searchByTitleOrDescription(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Find learning paths by tag
     */
    @Query("SELECT lp FROM LearningPath lp JOIN lp.tags t WHERE LOWER(t) = LOWER(:tag)")
    Page<LearningPath> findByTag(@Param("tag") String tag, Pageable pageable);
    
    /**
     * Find published learning paths
     */
    @Query("SELECT lp FROM LearningPath lp WHERE lp.status = 'PUBLISHED'")
    Page<LearningPath> findPublishedLearningPaths(Pageable pageable);
    
    /**
     * Find learning paths that a user is enrolled in
     */
    @Query("SELECT lp FROM LearningPath lp JOIN lp.enrolledUsers u WHERE u.id = :userId")
    Page<LearningPath> findEnrolledLearningPaths(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * Get all unique tags
     */
    @Query("SELECT DISTINCT t FROM LearningPath lp JOIN lp.tags t ORDER BY t")
    List<String> findAllTags();
    
    /**
     * Advanced search with multiple criteria
     */
    @Query("SELECT lp FROM LearningPath lp WHERE " +
           "(:searchTerm IS NULL OR " +
           "LOWER(lp.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(lp.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:difficulty IS NULL OR lp.difficulty = :difficulty) AND " +
           "(:status IS NULL OR lp.status = :status)")
    Page<LearningPath> findWithCriteria(
        @Param("searchTerm") String searchTerm,
        @Param("difficulty") LearningPath.Difficulty difficulty,
        @Param("status") LearningPath.Status status,
        Pageable pageable
    );
}
