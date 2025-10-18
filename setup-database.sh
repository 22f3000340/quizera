#!/bin/bash

# Quizera Database Setup Script

echo "========================================="
echo "  Quizera Database Setup"
echo "========================================="
echo ""

# Check if MySQL is installed
if ! command -v mysql &> /dev/null
then
    echo "❌ MySQL is not installed. Please install MySQL first."
    echo "   Ubuntu/Debian: sudo apt-get install mysql-server"
    echo "   Fedora/RHEL: sudo dnf install mysql-server"
    exit 1
fi

echo "✅ MySQL is installed"
echo ""

# Prompt for MySQL credentials
read -p "Enter MySQL root username [root]: " MYSQL_USER
MYSQL_USER=${MYSQL_USER:-root}

read -sp "Enter MySQL password: " MYSQL_PASSWORD
echo ""
echo ""

# Test connection
echo "Testing MySQL connection..."
mysql -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "SELECT 1;" &> /dev/null

if [ $? -ne 0 ]; then
    echo "❌ Failed to connect to MySQL. Please check your credentials."
    exit 1
fi

echo "✅ MySQL connection successful"
echo ""

# Create database and tables
echo "Creating database and tables..."
mysql -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" << EOF
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
INSERT IGNORE INTO users (username, email, password, role) VALUES 
('admin', 'admin@quizera.com', 'admin123', 'ADMIN'),
('student', 'student@quizera.com', 'student123', 'STUDENT'),
('john_doe', 'john@example.com', 'password123', 'STUDENT'),
('jane_smith', 'jane@example.com', 'password123', 'STUDENT'),
('admin2', 'admin2@quizera.com', 'admin123', 'ADMIN');

SELECT 'Database and tables created successfully!' as Status;
SELECT COUNT(*) as 'Total Users' FROM users;
EOF

if [ $? -eq 0 ]; then
    echo ""
    echo "========================================="
    echo "✅ Database setup completed successfully!"
    echo "========================================="
    echo ""
    echo "Test Credentials:"
    echo "  Admin:   username=admin,   password=admin123"
    echo "  Student: username=student, password=student123"
    echo ""
    echo "You can now run the application using:"
    echo "  ./mvnw javafx:run"
    echo ""
else
    echo "❌ Failed to create database. Please check the error messages above."
    exit 1
fi
