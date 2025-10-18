# Quizera - Quick Start Guide

## 🚀 Quick Setup (3 Steps)

### Step 1: Setup Database

Run the automated setup script:
```bash
./setup-database.sh
```

Or manually using MySQL:
```bash
mysql -u root -p < src/main/resources/database/setup.sql
```

### Step 2: Configure Database Connection (Optional)

If your MySQL credentials are different from the defaults, edit:
`src/main/java/com/quiz/quizera/utils/DatabaseConnection.java`

```java
private static final String URL = "jdbc:mysql://localhost:3306/quizera";
private static final String USER = "root";      // Change this
private static final String PASSWORD = "root";  // Change this
```

### Step 3: Run the Application

```bash
./mvnw clean javafx:run
```

## 🔑 Default Login Credentials

### Admin Account
```
Username: admin
Password: admin123
```

### Student Account
```
Username: student
Password: student123
```

## 📱 Application Features

### Login Screen
- Login with username and password
- Navigate to registration
- Test credentials displayed on screen

### Registration Screen
- Create new account
- Choose role (Student/Admin)
- Email validation
- Password confirmation

### Admin Dashboard
- View all users in a table
- Delete users
- Refresh user list
- Logout

### Student Dashboard
- View profile (placeholder)
- View courses (placeholder)
- View grades (placeholder)
- Logout

## 🛠️ Troubleshooting

### "Connection refused" error
```bash
# Check if MySQL is running
sudo systemctl status mysql

# Start MySQL if not running
sudo systemctl start mysql
```

### "Access denied" error
- Check your MySQL credentials in `DatabaseConnection.java`
- Verify you can login: `mysql -u root -p`

### Maven/JavaFX errors
```bash
# Clean and rebuild
./mvnw clean install

# If issues persist, delete target folder
rm -rf target/
./mvnw clean compile javafx:run
```

## 📊 Database Schema

```sql
users
├── id (INT, PRIMARY KEY, AUTO_INCREMENT)
├── username (VARCHAR(50), UNIQUE)
├── email (VARCHAR(100), UNIQUE)
├── password (VARCHAR(255))
├── role (ENUM: 'STUDENT', 'ADMIN')
└── created_at (TIMESTAMP)
```

## 🔄 Workflow

1. User opens application → Login screen appears
2. User logs in with credentials
3. System validates credentials against database
4. If role = ADMIN → Admin Dashboard
5. If role = STUDENT → Student Dashboard
6. User can logout and return to login screen

## 📝 Notes

- Passwords are stored in plain text (NOT recommended for production)
- For production, implement BCrypt or similar password hashing
- MySQL connector is included in pom.xml
- JavaFX 21 is used for UI components

## 🎯 Next Steps

After basic setup, you can:
1. Add quiz functionality
2. Implement password encryption
3. Add more admin features (edit users, view statistics)
4. Add student features (take quizzes, view results)
5. Implement email verification
6. Add profile picture upload

---

**Need Help?** Check README.md for detailed documentation.
