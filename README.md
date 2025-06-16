# PrimusLearning - Personalized Learning Path Management System

A comprehensive Spring Boot web application for managing personalized learning paths with user authentication, role-based access control, and a responsive user interface.

## 🚀 Features

### Core Functionality
- **User Management**
  - User registration with email validation
  - Secure login with password hashing (BCrypt)
  - Role-based access control (User, Admin)
  - Session management and logout functionality

- **Learning Path Management**
  - Create new learning paths with title, description, and estimated duration
  - View list of all available learning paths with pagination
  - Update existing learning path content and metadata
  - Delete learning paths with confirmation
  - Associate learning paths with categories/tags
  - Enroll/unenroll in learning paths

- **User Interface**
  - Responsive dashboard layout
  - Clean navigation menu
  - Form validation with user feedback
  - Search and filter capabilities for learning paths
  - Mobile-friendly design with Bootstrap 5.x

### Admin Features
- Admin dashboard with system statistics
- User management (enable/disable, promote to admin, delete)
- Learning path moderation (publish, archive, delete)
- System overview and analytics

## 🛠️ Technical Stack

- **Backend Framework**: Spring Boot 3.x
- **Build Tool**: Maven
- **Security**: Spring Security 6.x
- **Data Access**: Spring Data JPA
- **Database**: H2 in-memory database
- **Template Engine**: Thymeleaf
- **Frontend**: Bootstrap 5.x with responsive design
- **Testing**: JUnit 5, Mockito, Spring Boot Test

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- IDE (IntelliJ IDEA, Eclipse, or VS Code recommended)

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
cd jenkins-project
```

### 2. Build the Application
```bash
mvn clean compile
```

### 3. Run Tests
```bash
mvn test
```

### 4. Start the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 5. Access H2 Database Console
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:primuslearning`
- Username: `sa`
- Password: `password`

## 👥 Demo Accounts

The application comes with pre-configured demo accounts:

### Admin Account
- **Username**: `admin`
- **Password**: `admin123`
- **Access**: Full system access including admin panel

### Regular User Account
- **Username**: `demo`
- **Password**: `demo123`
- **Access**: Standard user features

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/primuslearning/
│   │   ├── PrimusLearningApplication.java
│   │   ├── config/
│   │   │   ├── SecurityConfig.java
│   │   │   └── DataInitializer.java
│   │   ├── controller/
│   │   │   ├── HomeController.java
│   │   │   ├── AuthController.java
│   │   │   ├── LearningPathController.java
│   │   │   └── AdminController.java
│   │   ├── dto/
│   │   │   ├── UserRegistrationDto.java
│   │   │   └── LearningPathDto.java
│   │   ├── entity/
│   │   │   ├── User.java
│   │   │   └── LearningPath.java
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   └── LearningPathRepository.java
│   │   └── service/
│   │       ├── UserService.java
│   │       └── LearningPathService.java
│   └── resources/
│       ├── application.properties
│       └── templates/
│           ├── layout.html
│           ├── index.html
│           ├── dashboard.html
│           ├── auth/
│           │   ├── login.html
│           │   └── register.html
│           ├── learning-path/
│           │   ├── list.html
│           │   ├── detail.html
│           │   └── create.html
│           └── admin/
└── test/
    └── java/com/primuslearning/
        ├── PrimusLearningApplicationTests.java
        ├── service/
        │   └── UserServiceTest.java
        └── controller/
            └── HomeControllerTest.java
```

## 🔧 Configuration

### Database Configuration
The application uses H2 in-memory database by default. To use a different database, update `application.properties`:

```properties
# For MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/primuslearning
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

### Security Configuration
- Session-based authentication
- CSRF protection enabled (except for H2 console)
- Role-based access control
- Password encoding with BCrypt

## 🧪 Testing

Run the test suite:
```bash
mvn test
```

The application includes:
- Unit tests for services
- Integration tests for controllers
- Security configuration tests

## 📊 API Endpoints

### Public Endpoints
- `GET /` - Home page
- `GET /login` - Login page
- `GET /register` - Registration page
- `GET /learning-paths` - Public learning paths list
- `GET /learning-paths/{id}` - Learning path details

### User Endpoints (Requires Authentication)
- `GET /dashboard` - User dashboard
- `GET /learning-paths/my-paths` - User's created learning paths
- `GET /learning-paths/my-enrollments` - User's enrolled learning paths
- `POST /learning-paths/create` - Create new learning path
- `POST /learning-paths/enroll/{id}` - Enroll in learning path
- `POST /learning-paths/unenroll/{id}` - Unenroll from learning path

### Admin Endpoints (Requires Admin Role)
- `GET /admin/dashboard` - Admin dashboard
- `GET /admin/users` - User management
- `GET /admin/learning-paths` - Learning path management
- `POST /admin/users/{id}/toggle-status` - Enable/disable user
- `POST /admin/learning-paths/{id}/publish` - Publish learning path

## 🎨 UI Features

- **Responsive Design**: Works on desktop, tablet, and mobile devices
- **Modern UI**: Clean, professional interface with Bootstrap 5
- **Interactive Elements**: Dynamic forms, modals, and alerts
- **Accessibility**: ARIA labels and keyboard navigation support
- **Performance**: Optimized loading and minimal JavaScript

## 🔒 Security Features

- **Authentication**: Secure login/logout with session management
- **Authorization**: Role-based access control (RBAC)
- **Password Security**: BCrypt hashing with salt
- **CSRF Protection**: Enabled for all state-changing operations
- **Input Validation**: Server-side validation for all forms
- **SQL Injection Prevention**: JPA with parameterized queries

## 📈 Performance Considerations

- **Database**: Efficient queries with proper indexing
- **Pagination**: Server-side pagination for large datasets
- **Caching**: Template caching in production
- **Compression**: Gzip compression for static resources

## 🚀 Deployment

### Local Development
```bash
mvn spring-boot:run
```

### Production Build
```bash
mvn clean package
java -jar target/primus-learning-0.0.1-SNAPSHOT.jar
```

### Docker (Optional)
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/primus-learning-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Create a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the documentation
- Review the demo accounts and sample data

## 🔮 Future Enhancements

- **Progress Tracking**: Track learning progress and completion
- **Certificates**: Generate completion certificates
- **Social Features**: User profiles and social learning
- **Content Management**: Rich text editor for learning path content
- **Analytics**: Detailed learning analytics and reporting
- **Mobile App**: Native mobile application
- **Integration**: LMS and third-party service integrations

---

**PrimusLearning** - Empowering personalized learning journeys for everyone.
