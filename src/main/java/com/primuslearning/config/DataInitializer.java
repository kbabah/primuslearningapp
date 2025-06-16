package com.primuslearning.config;

import com.primuslearning.entity.LearningPath;
import com.primuslearning.entity.User;
import com.primuslearning.service.LearningPathService;
import com.primuslearning.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Data initialization component to populate sample data
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    private final UserService userService;
    private final LearningPathService learningPathService;
    
    @Autowired
    public DataInitializer(UserService userService, LearningPathService learningPathService) {
        this.userService = userService;
        this.learningPathService = learningPathService;
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Create admin user
        if (!userService.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@primuslearning.com");
            admin.setPassword("admin123");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole(User.Role.ADMIN);
            userService.registerUser(admin);
            System.out.println("Created admin user: admin/admin123");
        }
        
        // Create demo user
        if (!userService.existsByUsername("demo")) {
            User demo = new User();
            demo.setUsername("demo");
            demo.setEmail("demo@primuslearning.com");
            demo.setPassword("demo123");
            demo.setFirstName("Demo");
            demo.setLastName("User");
            demo.setRole(User.Role.USER);
            userService.registerUser(demo);
            System.out.println("Created demo user: demo/demo123");
        }
        
        // Create sample learning paths
        User admin = userService.findByUsername("admin").orElse(null);
        User demo = userService.findByUsername("demo").orElse(null);
        
        if (admin != null && learningPathService.getAllLearningPaths(
                org.springframework.data.domain.PageRequest.of(0, 1)).isEmpty()) {
            
            // Java Learning Path
            LearningPath javaPath = new LearningPath();
            javaPath.setTitle("Complete Java Programming Course");
            javaPath.setDescription("Master Java programming from basics to advanced concepts. " +
                "This comprehensive course covers object-oriented programming, data structures, " +
                "algorithms, and modern Java features.");
            javaPath.setEstimatedDurationHours(40);
            javaPath.setDifficulty(LearningPath.Difficulty.BEGINNER);
            javaPath.setStatus(LearningPath.Status.PUBLISHED);
            javaPath.setCreator(admin);
            javaPath.addTag("java");
            javaPath.addTag("programming");
            javaPath.addTag("backend");
            learningPathService.createLearningPath(javaPath);
            
            // Spring Boot Learning Path
            LearningPath springPath = new LearningPath();
            springPath.setTitle("Spring Boot Microservices");
            springPath.setDescription("Learn to build scalable microservices using Spring Boot. " +
                "Covers REST APIs, database integration, security, and deployment strategies.");
            springPath.setEstimatedDurationHours(35);
            springPath.setDifficulty(LearningPath.Difficulty.INTERMEDIATE);
            springPath.setStatus(LearningPath.Status.PUBLISHED);
            springPath.setCreator(admin);
            springPath.addTag("spring-boot");
            springPath.addTag("microservices");
            springPath.addTag("rest-api");
            learningPathService.createLearningPath(springPath);
            
            // Frontend Learning Path
            LearningPath frontendPath = new LearningPath();
            frontendPath.setTitle("Modern Frontend Development");
            frontendPath.setDescription("Build modern web applications using HTML5, CSS3, JavaScript, " +
                "and popular frameworks like React. Learn responsive design and modern development tools.");
            frontendPath.setEstimatedDurationHours(50);
            frontendPath.setDifficulty(LearningPath.Difficulty.BEGINNER);
            frontendPath.setStatus(LearningPath.Status.PUBLISHED);
            frontendPath.setCreator(demo);
            frontendPath.addTag("html");
            frontendPath.addTag("css");
            frontendPath.addTag("javascript");
            frontendPath.addTag("react");
            learningPathService.createLearningPath(frontendPath);
            
            // Data Science Learning Path
            LearningPath dataPath = new LearningPath();
            dataPath.setTitle("Data Science with Python");
            dataPath.setDescription("Dive into data science using Python. Learn pandas, numpy, " +
                "matplotlib, machine learning algorithms, and data visualization techniques.");
            dataPath.setEstimatedDurationHours(60);
            dataPath.setDifficulty(LearningPath.Difficulty.ADVANCED);
            dataPath.setStatus(LearningPath.Status.PUBLISHED);
            dataPath.setCreator(admin);
            dataPath.addTag("python");
            dataPath.addTag("data-science");
            dataPath.addTag("machine-learning");
            dataPath.addTag("analytics");
            learningPathService.createLearningPath(dataPath);
            
            // DevOps Learning Path
            LearningPath devopsPath = new LearningPath();
            devopsPath.setTitle("DevOps Fundamentals");
            devopsPath.setDescription("Master DevOps practices including CI/CD, containerization " +
                "with Docker, orchestration with Kubernetes, and cloud deployment strategies.");
            devopsPath.setEstimatedDurationHours(45);
            devopsPath.setDifficulty(LearningPath.Difficulty.INTERMEDIATE);
            devopsPath.setStatus(LearningPath.Status.DRAFT);
            devopsPath.setCreator(demo);
            devopsPath.addTag("devops");
            devopsPath.addTag("docker");
            devopsPath.addTag("kubernetes");
            devopsPath.addTag("ci-cd");
            learningPathService.createLearningPath(devopsPath);
            
            System.out.println("Created sample learning paths");
        }
    }
}
