# Quizera Application Flow

## 🔄 Application Navigation Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                      APPLICATION START                           │
│                     (Launcher.java)                              │
└───────────────────────────────┬─────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                      LOGIN SCREEN                                │
│                   (login-view.fxml)                              │
│                 (LoginController.java)                           │
│                                                                   │
│  • Username Field                                                │
│  • Password Field                                                │
│  • [Login Button]                                                │
│  • [Register Link]                                               │
│  • Test Credentials Displayed                                    │
└────────────────┬────────────────────────────┬────────────────────┘
                 │                            │
       [Register]│                            │[Login]
                 │                            │
                 ▼                            ▼
┌───────────────────────────┐    ┌──────────────────────────────┐
│   REGISTRATION SCREEN     │    │   DATABASE VALIDATION        │
│  (register-view.fxml)     │    │  (DatabaseConnection.java)   │
│ (RegisterController.java) │    │                              │
│                           │    │  SELECT * FROM users         │
│ • Username                │    │  WHERE username = ?          │
│ • Email                   │    │  AND password = ?            │
│ • Password                │    └──────────┬───────────────────┘
│ • Confirm Password        │               │
│ • Role Selection          │               │
│ • [Register Button]       │     ┌─────────┴──────────┐
│ • [Back to Login]         │     │                    │
└───────────┬───────────────┘     │              ┌─────▼─────────┐
            │                     │              │  Role Check   │
            │[Success]            │              │  role = ?     │
            └─────────────────────┘              └──┬────────┬───┘
                                                    │        │
                                          [ADMIN]   │        │   [STUDENT]
                                                    │        │
                    ┌───────────────────────────────┘        └──────────────────────────┐
                    ▼                                                                    ▼
┌────────────────────────────────────────────┐              ┌────────────────────────────────────┐
│         ADMIN DASHBOARD                    │              │      STUDENT DASHBOARD             │
│        (admin-view.fxml)                   │              │     (student-view.fxml)            │
│      (AdminController.java)                │              │   (StudentController.java)         │
│                                            │              │                                    │
│  Header:                                   │              │  Header:                           │
│  • Welcome Label (username + role)         │              │  • Welcome Label (username + role) │
│  • [Logout Button]                         │              │  • [Logout Button]                 │
│                                            │              │                                    │
│  Content:                                  │              │  Content:                          │
│  • User Management Table                   │              │  • [View Profile]                  │
│    ┌─────┬──────────┬────────────┬──────┐ │              │  • [View Courses]                  │
│    │ ID  │ Username │ Email      │ Role │ │              │  • [View Grades]                   │
│    ├─────┼──────────┼────────────┼──────┤ │              │                                    │
│    │ 1   │ admin    │ admin@...  │ ADMIN│ │              │  (All features are placeholders    │
│    │ 2   │ student  │ student@.. │ STU..│ │              │   for future implementation)       │
│    │ ... │ ...      │ ...        │ ...  │ │              │                                    │
│    └─────┴──────────┴────────────┴──────┘ │              └────────────────────────────────────┘
│                                            │                             │
│  Actions:                                  │                             │
│  • [Refresh Users] - Reload user list     │                             │
│  • [Delete User] - Remove selected user   │                             │
│                                            │                             │
└────────────────────────────────────────────┘                             │
                    │                                                      │
                    │[Logout]                                    [Logout]  │
                    └──────────────────────┬───────────────────────────────┘
                                           │
                                           ▼
                              ┌────────────────────────┐
                              │   BACK TO LOGIN SCREEN │
                              └────────────────────────┘
```

## 📊 Database Interaction Flow

```
┌──────────────────┐
│  Application     │
│  Controllers     │
└────────┬─────────┘
         │
         │ Uses
         ▼
┌─────────────────────────────┐
│  DatabaseConnection.java    │
│                             │
│  getConnection()            │
│    ↓                        │
│  DriverManager              │
│    .getConnection(          │
│      URL, USER, PASSWORD)   │
└────────┬────────────────────┘
         │
         │ JDBC Connection
         ▼
┌─────────────────────────────┐
│    MySQL Database           │
│    (quizera)                │
│                             │
│  Tables:                    │
│  • users                    │
│    - id                     │
│    - username               │
│    - email                  │
│    - password               │
│    - role                   │
│    - created_at             │
└─────────────────────────────┘
```

## 🔐 Authentication Flow

```
User enters credentials
         ↓
LoginController.handleLogin()
         ↓
Validate input (not empty)
         ↓
DatabaseConnection.getConnection()
         ↓
Execute SQL: SELECT * FROM users 
           WHERE username = ? AND password = ?
         ↓
    ┌────┴─────┐
    │          │
 Found      Not Found
    │          │
    ↓          ↓
Get role   Show error
    │
    ├── ADMIN → Load admin-view.fxml → AdminController.initData()
    │
    └── STUDENT → Load student-view.fxml → StudentController.initData()
```

## 📝 Registration Flow

```
User fills registration form
         ↓
RegisterController.handleRegister()
         ↓
Validate input (all fields filled)
         ↓
Check password == confirmPassword
         ↓
DatabaseConnection.getConnection()
         ↓
Execute SQL: INSERT INTO users 
           (username, email, password, role) 
           VALUES (?, ?, ?, ?)
         ↓
    ┌────┴─────┐
    │          │
 Success    Error
    │          │
    ↓          ↓
Redirect   Show error
to Login   message
```

## 🗑️ Admin Delete User Flow

```
Admin selects user from table
         ↓
Admin clicks "Delete User"
         ↓
AdminController.handleDeleteUser()
         ↓
Show confirmation dialog
         ↓
    ┌────┴─────┐
    │          │
   OK       Cancel
    │          │
    ↓          └──→ (Do nothing)
Execute DELETE
    ↓
DELETE FROM users WHERE id = ?
    ↓
Refresh table (loadUsers())
```

## 🔄 Component Communication

```
FXML Files (Views)
    ↕ fx:controller
Controller Classes (Logic)
    ↕ DatabaseConnection
Database Layer (MySQL)
    ↕ JDBC Driver
MySQL Server
```

## 🎨 UI Component Hierarchy

```
VBox (Root) - login-view.fxml
├── VBox (Container with white background)
│   ├── Label (Title: "🎓 Quizera")
│   ├── Label (Subtitle)
│   ├── VBox (Form fields)
│   │   ├── VBox (Username group)
│   │   │   ├── Label
│   │   │   └── TextField (fx:id="usernameField")
│   │   └── VBox (Password group)
│   │       ├── Label
│   │       └── PasswordField (fx:id="passwordField")
│   ├── Button (Login - onAction="#handleLogin")
│   ├── Separator
│   ├── HBox (Register link)
│   ├── Label (fx:id="messageLabel")
│   └── VBox (Test credentials display)
```

---

This flow diagram shows how all components work together to create a complete authentication and user management system!
