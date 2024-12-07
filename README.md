# Library Management System

A Library Management System designed to streamline and optimize library operations. This project enables efficient management of books, customers, borrow records, and librarians through a MySQL database. The system supports core functionalities like adding, removing, and updating records, ensuring librarians can manage resources efficiently while providing excellent service to users.

## Features

### Books Management:
- View all books.
- Search books by title.
- Check availability of books.
- Add new books.
- Remove books.
- Update the status of books (e.g., Available, Borrowed).

### Customer Management:
- View all customers.
- Add new customers.
- Remove customers.

### Borrow Records:
- View borrow records, including customer and book details.
- Add new borrow records.
- Remove borrow records.
- Identify overdue books.

### Librarian Management:
- View all librarians.
- Add new librarians.
- Remove librarians.

---

## Database Schema

### Tables

1. **Category**:
   - Stores book categories (e.g., Fiction, Non-Fiction).
   - **Columns**: `CategoryID`, `CategoryName`.

2. **Books**:
   - Stores book details and their availability status.
   - **Columns**: `BookID`, `Title`, `Author`, `ISBN`, `Publisher`, `PublishedYear`, `CategoryID`, `Status`.

3. **Customer**:
   - Stores customer information.
   - **Columns**: `CustomerID`, `FullName`, `Address`, `PhoneNumber`, `Email`, `MembershipDate`.

4. **BorrowRecords**:
   - Tracks borrowing transactions.
   - **Columns**: `RecordID`, `CustomerID`, `BookID`, `BorrowDate`, `ReturnDate`, `Status`.

5. **Librarians**:
   - Stores librarian details.
   - **Columns**: `LibrarianID`, `FullName`, `PhoneNumber`, `Email`, `HireDate`.
