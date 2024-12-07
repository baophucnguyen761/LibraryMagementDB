-- Step 1: Create the Database
CREATE DATABASE LibraryManagementSystem;
USE LibraryManagementSystem;

-- 1. Category Table
CREATE TABLE Category (
    CategoryID INT AUTO_INCREMENT PRIMARY KEY,
    CategoryName VARCHAR(100) NOT NULL
);

-- 2. Books Table
CREATE TABLE Books (
    BookID INT AUTO_INCREMENT PRIMARY KEY,
    Title VARCHAR(255) NOT NULL,
    Author VARCHAR(255) NOT NULL,
    ISBN VARCHAR(20) NOT NULL UNIQUE,
    Publisher VARCHAR(255),
    PublishedYear INT,
    CategoryID INT,
    Status ENUM('Available', 'Borrowed') DEFAULT 'Available',
    FOREIGN KEY (CategoryID) REFERENCES Category(CategoryID)
);

-- 3. Customer Table
CREATE TABLE Customer (
    CustomerID INT AUTO_INCREMENT PRIMARY KEY,
    FullName VARCHAR(255) NOT NULL,
    Address VARCHAR(255),
    PhoneNumber VARCHAR(15),
    Email VARCHAR(255) UNIQUE,
    MembershipDate DATE NOT NULL
);

-- 4. BorrowRecords Table
CREATE TABLE BorrowRecords (
    RecordID INT AUTO_INCREMENT PRIMARY KEY,
    CustomerID INT NOT NULL,
    BookID INT NOT NULL,
    BorrowDate DATE NOT NULL,
    ReturnDate DATE,
    Status ENUM('Returned', 'Overdue') DEFAULT 'Returned',
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID),
    FOREIGN KEY (BookID) REFERENCES Books(BookID)
);

-- 5. Librarians Tables
CREATE TABLE Librarians (
    LibrarianID INT AUTO_INCREMENT PRIMARY KEY,
    FullName VARCHAR(255) NOT NULL,
    PhoneNumber VARCHAR(15),
    Email VARCHAR(255) UNIQUE,
    HireDate DATE NOT NULL
);


-- Insert Categories
INSERT INTO Category (CategoryName) VALUES 
('Fiction'), 
('Non-Fiction'), 
('Science'), 
('History');

-- Insert Books
INSERT INTO Books (Title, Author, ISBN, Publisher, PublishedYear, CategoryID, Status) VALUES 
('1984', 'George Orwell', '9780451524935', 'Harcourt Brace Jovanovich', 1949, 1, 'Available'),
('The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565', 'Charles Scribner\'s Sons', 1925, 1, 'Borrowed'),
('The Origin of Species', 'Charles Darwin', '9781509827695', 'John Murray', 1859, 3, 'Available'),
('The Art of War', 'Sun Tzu', '9781590302255', 'Shambhala', -500, 4, 'Available'),
('Sapiens: A Brief History of Humankind', 'Yuval Noah Harari', '9780062316110', 'Harper', 2014, 2, 'Borrowed'),
('Cosmos', 'Carl Sagan', '9780345331359', 'Random House', 1980, 3, 'Available'),
('Pride and Prejudice', 'Jane Austen', '9780141439518', 'T. Egerton', 1813, 1, 'Available'),
('A Song of Ice and Fire', 'George R.R. Martin', '9780553103540', 'Bantam Spectra', 1996, 1, 'Borrowed');     

-- Insert Customers
INSERT INTO Customer (FullName, Address, PhoneNumber, Email, MembershipDate) VALUES 
('Alice Johnson', '123 Maple St.', '555-1234', 'alice@example.com', '2024-01-15'),
('Bob Smith', '456 Oak St.', '555-5678', 'bob@example.com', '2024-02-10');

-- Insert Borrow Records
INSERT INTO BorrowRecords (CustomerID, BookID, BorrowDate, ReturnDate, Status) VALUES 
(1, 1, '2024-12-01', '2024-12-15', 'Returned'),
(2, 2, '2024-12-01', NULL, 'Overdue');

-- Insert Librarians
INSERT INTO Librarians (FullName, PhoneNumber, Email, HireDate) VALUES
('Alice Johnson', '555-1234', 'alice.johnson@library.com', '2020-01-15'),
('Bob Smith', '555-5678', 'bob.smith@library.com', '2018-05-10'),
('Catherine Lee', '555-9876', 'catherine.lee@library.com', '2021-03-22');


-- Step 4: Queries

# 1. View All Books
SELECT * FROM Books;

# 2. Search for Books by Title.
SELECT * FROM Books WHERE Title LIKE '%Mockingbird%';

# 3. Check Availability of Books.
SELECT Title, Status FROM Books WHERE Status = 'Available';

-- 4. View Borrow Records
SELECT b.RecordID, c.FullName, bo.Title, b.BorrowDate, b.ReturnDate, b.Status
FROM BorrowRecords b
JOIN Customer c ON b.CustomerID = c.CustomerID
JOIN Books bo ON b.BookID = bo.BookID;

-- 5. List Overdue Books
SELECT c.FullName, bo.Title, b.BorrowDate, b.Status
FROM BorrowRecords b
JOIN Customer c ON b.CustomerID = c.CustomerID
JOIN Books bo ON b.BookID = bo.BookID
WHERE b.Status = 'Overdue';

-- 6. Add a Book
INSERT INTO Books (Title, Author, ISBN, Publisher, PublishedYear, CategoryID, Status) 
VALUES (?,?,?,?,?,?,?);

-- 7. Remove a Book
DELETE FROM Books 
WHERE BookID = ?; --Replace ? with the ID of the book you want to remove.

-- 8. Add a Customer
INSERT INTO Customer (FullName, Address, PhoneNumber, Email, MembershipDate) 
VALUES (?,?,?,?,?);

-- 9. Remove a Customer
DELETE FROM Customer 
WHERE CustomerID = ?; - Replace ? with the ID of the customer you want to remove.

-- 10. Add a Librarian
INSERT INTO Librarians (FullName, PhoneNumber, Email, HireDate) 
VALUES (?,?,?,?);

-- 11. Remove a Librarian
DELETE FROM Librarians 
WHERE LibrarianID = ?; 

-- 12. Add a Borrow Record
INSERT INTO BorrowRecords (CustomerID, BookID, BorrowDate, ReturnDate, Status) 
VALUES (?,?,?,?,?);

-- 13. Remove a Borrow Record
DELETE FROM BorrowRecords 
WHERE RecordID = ?; 

-- 14. Update Book Status
UPDATE Books 
SET Status = ? 
WHERE BookID = ?;
