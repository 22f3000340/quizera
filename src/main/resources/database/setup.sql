-- Create database
CREATE DATABASE IF NOT EXISTS quizera;
USE quizera;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'ADMIN') NOT NULL DEFAULT 'STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Categories
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

-- Questions
CREATE TABLE IF NOT EXISTS questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    difficulty ENUM('EASY','MEDIUM','HARD') NOT NULL,
    question_text TEXT NOT NULL,
    option_a VARCHAR(255) NOT NULL,
    option_b VARCHAR(255) NOT NULL,
    option_c VARCHAR(255) NOT NULL,
    option_d VARCHAR(255) NOT NULL,
    correct_option ENUM('A','B','C','D') NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- Quiz Attempts
CREATE TABLE IF NOT EXISTS quiz_attempts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    difficulty ENUM('EASY','MEDIUM','HARD') NOT NULL,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    score INT DEFAULT 0,
    total INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- Attempt Answers
CREATE TABLE IF NOT EXISTS attempt_answers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    attempt_id INT NOT NULL,
    question_id INT NOT NULL,
    selected_option ENUM('A','B','C','D') NOT NULL,
    is_correct BOOLEAN NOT NULL,
    FOREIGN KEY (attempt_id) REFERENCES quiz_attempts(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

-- Insert test users
-- Password: admin123
INSERT INTO users (username, email, password, role) VALUES 
('admin', 'admin@quizera.com', 'admin123', 'ADMIN');

-- Password: student123
INSERT INTO users (username, email, password, role) VALUES 
('student', 'student@quizera.com', 'student123', 'STUDENT');

-- Additional test users
INSERT INTO users (username, email, password, role) VALUES 
('john_doe', 'john@example.com', 'password123', 'STUDENT'),
('jane_smith', 'jane@example.com', 'password123', 'STUDENT'),
('admin2', 'admin2@quizera.com', 'admin123', 'ADMIN');

-- Seed categories (ignore duplicates)
INSERT IGNORE INTO categories (name) VALUES ('General Knowledge'), ('Science'), ('Math');

-- Get category ids into variables (works when running directly in MySQL CLI)
SET @cat_gk = (SELECT id FROM categories WHERE name='General Knowledge');
SET @cat_sci = (SELECT id FROM categories WHERE name='Science');

-- Seed at least 10 EASY questions for General Knowledge
INSERT INTO questions (category_id, difficulty, question_text, option_a, option_b, option_c, option_d, correct_option) VALUES
(@cat_gk,'EASY','What color is the sky on a clear day?','Blue','Green','Red','Yellow','A'),
(@cat_gk,'EASY','How many days are in a week?','5','6','7','8','C'),
(@cat_gk,'EASY','What is H2O commonly known as?','Salt','Water','Oxygen','Hydrogen','B'),
(@cat_gk,'EASY','How many letters are in the English alphabet?','24','25','26','27','C'),
(@cat_gk,'EASY','Which animal is known as man''s best friend?','Cat','Dog','Horse','Cow','B'),
(@cat_gk,'EASY','Which month comes after June?','May','July','August','September','B'),
(@cat_gk,'EASY','What do bees make?','Milk','Honey','Silk','Bread','B'),
(@cat_gk,'EASY','How many continents are there?','5','6','7','8','C'),
(@cat_gk,'EASY','Which direction does the sun rise from?','North','South','East','West','C'),
(@cat_gk,'EASY','What do you call a baby cat?','Puppy','Kitten','Cub','Calf','B');

-- Seed at least 10 MEDIUM questions for General Knowledge
INSERT INTO questions (category_id, difficulty, question_text, option_a, option_b, option_c, option_d, correct_option) VALUES
(@cat_gk,'MEDIUM','Which planet is known as the Red Planet?','Earth','Mars','Jupiter','Venus','B'),
(@cat_gk,'MEDIUM','Who painted the Mona Lisa?','Van Gogh','Picasso','Da Vinci','Michelangelo','C'),
(@cat_gk,'MEDIUM','Which ocean is the largest?','Atlantic','Indian','Arctic','Pacific','D'),
(@cat_gk,'MEDIUM','Which country hosted the 2016 Summer Olympics?','China','Brazil','UK','Russia','B'),
(@cat_gk,'MEDIUM','What is the capital of Australia?','Sydney','Melbourne','Canberra','Perth','C'),
(@cat_gk,'MEDIUM','Who wrote "1984"?','George Orwell','Aldous Huxley','Mark Twain','J.K. Rowling','A'),
(@cat_gk,'MEDIUM','Which gas do plants absorb?','Oxygen','Nitrogen','Carbon Dioxide','Helium','C'),
(@cat_gk,'MEDIUM','Which metal is liquid at room temperature?','Mercury','Iron','Aluminum','Copper','A'),
(@cat_gk,'MEDIUM','What is the hardest natural substance?','Diamond','Graphite','Quartz','Gold','A'),
(@cat_gk,'MEDIUM','Which continent is Egypt in?','Asia','Africa','Europe','South America','B');

-- Seed at least 10 HARD questions for Science
INSERT INTO questions (category_id, difficulty, question_text, option_a, option_b, option_c, option_d, correct_option) VALUES
(@cat_sci,'HARD','What is the SI unit of electric capacitance?','Weber','Farad','Tesla','Henry','B'),
(@cat_sci,'HARD','Which law relates pressure and temperature at constant volume?','Boyle''s Law','Charles''s Law','Gay-Lussac''s Law','Avogadro''s Law','C'),
(@cat_sci,'HARD','What particle mediates the strong nuclear force?','Gluon','Photon','Graviton','W Boson','A'),
(@cat_sci,'HARD','Which vitamin is also known as retinol?','Vitamin A','Vitamin C','Vitamin D','Vitamin K','A'),
(@cat_sci,'HARD','Which organelles contain their own DNA?','Ribosomes','Mitochondria','Golgi apparatus','Lysosomes','B'),
(@cat_sci,'HARD','What is Planck''s constant approximately?','6.63e-34 J·s','9.81 m/s^2','1.60e-19 C','3.00e8 m/s','A'),
(@cat_sci,'HARD','What is the pH of a neutral solution at 25°C?','5','7','9','11','B'),
(@cat_sci,'HARD','Which of these is an alkali metal?','Calcium','Magnesium','Sodium','Aluminum','C'),
(@cat_sci,'HARD','Which scientist proposed the uncertainty principle?','Einstein','Bohr','Heisenberg','Schrödinger','C'),
(@cat_sci,'HARD','The term "quark" was coined by?','Gell-Mann','Feynman','Dirac','Hawking','A');

-- Display all users
SELECT * FROM users;
