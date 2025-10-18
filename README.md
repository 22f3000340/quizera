# Quizera - JavaFX Quiz Application

A JavaFX-based quiz application with user authentication, admin panel, and student dashboard.

## Features

- **User Authentication**: Login and Registration system
- **Role-Based Access**: 
  - Admin users can manage all users (view, delete)
  - Student users have access to student dashboard
- **MySQL Database Integration**: Using JDBC for database operations
- **Modern UI**: Clean and attractive JavaFX interface

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- JavaFX 21

## Database Setup

1. **Install MySQL** (if not already installed)

2. **Create the database and tables**:
   ```bash
   mysql -u root -p < src/main/resources/database/setup.sql
   ```

   Or manually run the SQL commands:
   ```sql
   CREATE DATABASE IF NOT EXISTS quizera;
   USE quizera;

   CREATE TABLE IF NOT EXISTS users (
       id INT AUTO_INCREMENT PRIMARY KEY,
       username VARCHAR(50) UNIQUE NOT NULL,
       email VARCHAR(100) UNIQUE NOT NULL,
       password VARCHAR(255) NOT NULL,
       role ENUM('STUDENT', 'ADMIN') NOT NULL DEFAULT 'STUDENT',
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   );

   -- Insert test users
   INSERT INTO users (username, email, password, role) VALUES 
   ('admin', 'admin@quizera.com', 'admin123', 'ADMIN'),
   ('student', 'student@quizera.com', 'student123', 'STUDENT');
   ```

3. **Update Database Credentials** (if needed):
   
   Edit `src/main/java/com/quiz/quizera/utils/DatabaseConnection.java`:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/quizera";
   private static final String USER = "root";  // Change if needed
   private static final String PASSWORD = "root";  // Change to your MySQL password
   ```

## Running the Application

### Using Maven

1. **Clean and compile**:
   ```bash
   ./mvnw clean compile
   ```

2. **Run the application**:
   ```bash
   ./mvnw javafx:run
   ```

### Using IDE (IntelliJ IDEA / Eclipse)

1. Import the project as a Maven project
2. Wait for dependencies to download
3. Run the `Launcher.java` class

## Test Credentials

### Admin User
- **Username**: `admin`
- **Password**: `admin123`

### Student User
- **Username**: `student`
- **Password**: `student123`

## Project Structure

```
quizera/
├── src/main/
│   ├── java/com/quiz/quizera/
│   │   ├── HelloApplication.java      # Main JavaFX Application
│   │   ├── Launcher.java              # Application entry point
│   │   ├── controllers/
│   │   │   ├── LoginController.java   # Handles login logic
│   │   │   ├── RegisterController.java # Handles registration
│   │   │   ├── AdminController.java   # Admin dashboard
│   │   │   └── StudentController.java # Student dashboard
│   │   ├── models/
│   │   │   └── User.java              # User model
│   │   └── utils/
│   │       └── DatabaseConnection.java # Database connection utility
│   └── resources/com/quiz/quizera/
│       ├── login-view.fxml            # Login UI
│       ├── register-view.fxml         # Registration UI
│       ├── admin-view.fxml            # Admin dashboard UI
│       ├── student-view.fxml          # Student dashboard UI
│       └── database/
│           └── setup.sql              # Database setup script
├── pom.xml                            # Maven configuration
└── README.md                          # This file
```

## Features by Role

### Admin Dashboard
- View all registered users
- Delete users from the system
- Refresh user list
- Logout functionality

### Student Dashboard
- View profile (placeholder)
- View courses (placeholder)
- View grades (placeholder)
- Logout functionality

## Troubleshooting

### Database Connection Issues
- Ensure MySQL is running: `sudo systemctl status mysql`
- Verify database exists: `mysql -u root -p -e "SHOW DATABASES;"`
- Check credentials in `DatabaseConnection.java`

### Module Issues
- Ensure Java 17+ is being used
- Check that `module-info.java` is properly configured

### JavaFX Runtime Issues
- Make sure JavaFX dependencies are downloaded
- Try running: `./mvnw clean install` first

## Future Enhancements

- Password encryption (BCrypt)
- Quiz creation and management
- Question bank
- Student quiz attempts and scoring
- Grade reports
- Email verification
- Password reset functionality

## License

This project is for educational purposes.

## Author

Quizera Development Team
