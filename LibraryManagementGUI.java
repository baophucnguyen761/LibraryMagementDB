import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class LibraryManagementGUI {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/LibraryManagementSystem";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root1234";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Library Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 400);

            // Main panel with BoxLayout for vertical and horizontal centering
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

            // Add vertical glue to push buttons to the center
            mainPanel.add(Box.createVerticalGlue());

            // Buttons for functionality
            JButton viewBooksButton = new JButton("View All Books");
            JButton viewBorrowRecordsButton = new JButton("View Borrow Records");
            JButton viewLibrariansButton = new JButton("View Librarians");
            JButton viewCustomersButton = new JButton("View Customers");

            // Center align the buttons
            viewBooksButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            viewBorrowRecordsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            viewLibrariansButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            viewCustomersButton.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Add buttons to the panel with spacing
            mainPanel.add(viewBooksButton);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing
            mainPanel.add(viewBorrowRecordsButton);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            mainPanel.add(viewLibrariansButton);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            mainPanel.add(viewCustomersButton);

            // Add vertical glue to push buttons to the center
            mainPanel.add(Box.createVerticalGlue());

            // Add main panel to the frame
            frame.add(mainPanel);

            // Button listeners
            viewBooksButton.addActionListener(e -> showBooksDialog());
            viewBorrowRecordsButton.addActionListener(e -> showBorrowRecordsDialog());
            viewLibrariansButton.addActionListener(e -> showLibrariansDialog());
            viewCustomersButton.addActionListener(e -> showCustomersDialog());

            // Center the frame on the screen
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }


    private static void showBooksDialog() {
        DefaultTableModel tableModel = new DefaultTableModel();
        String query = "SELECT * FROM Books";
        executeQueryAndLoadData(query, tableModel, new String[]{"BookID", "Title", "Author", "ISBN", "Publisher", "PublishedYear", "CategoryID", "Status"});

        // Create and display the modal dialog
        createDialog("View All Books", tableModel, "Add Book", "Remove Book");
    }

    private static void showBorrowRecordsDialog() {
        DefaultTableModel tableModel = new DefaultTableModel();
        String query = "SELECT * FROM BorrowRecords";
        executeQueryAndLoadData(query, tableModel, new String[]{"RecordID", "CustomerID", "BookID", "BorrowDate", "ReturnDate", "Status"});

        // Create and display the modal dialog
        createDialog("View Borrow Records", tableModel, "Add Borrow Record", "Remove Borrow Record");
    }

    private static void showLibrariansDialog() {
        DefaultTableModel tableModel = new DefaultTableModel();
        String query = "SELECT * FROM Librarians";
        executeQueryAndLoadData(query, tableModel, new String[]{"LibrarianID", "FullName", "PhoneNumber", "Email", "HireDate"});

        // Create and display the modal dialog
        createDialog("View Librarians", tableModel, "Add Librarian", "Remove Librarian");
    }

    private static void showCustomersDialog() {
        DefaultTableModel tableModel = new DefaultTableModel();
        String query = "SELECT * FROM Customer";
        executeQueryAndLoadData(query, tableModel, new String[]{"CustomerID", "FullName", "Address", "PhoneNumber", "Email", "MembershipDate"});

        // Create and display the modal dialog
        createDialog("View Customers", tableModel, "Add Customer", "Remove Customer");
    }

    private static void createDialog(String title, DefaultTableModel tableModel, String addButtonLabel, String removeButtonLabel) {
        // Create a dialog
        JDialog dialog = new JDialog((JFrame) null, title, true);
        dialog.setSize(1000, 600);
        dialog.setLayout(new BorderLayout());

        // Table for displaying data
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton(addButtonLabel);
        JButton removeButton = new JButton(removeButtonLabel);
        JButton backButton = new JButton("Back");

        // Add a "Change Status" button for Books only
        JButton changeStatusButton = null;
        if (title.contains("Books")) {
            changeStatusButton = new JButton("Change Status");
            buttonPanel.add(changeStatusButton);
            changeStatusButton.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int bookID = (int) tableModel.getValueAt(selectedRow, 0); // Get BookID from the selected row
                    changeBookStatus(bookID, tableModel, table); // Pass table for direct updates
                } else {
                    JOptionPane.showMessageDialog(dialog, "Please select a book to change its status.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }

        // Add buttons to the panel
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(backButton);

        // Add button panel to dialog
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Button listeners
        addButton.addActionListener(e -> {
            dialog.dispose(); // Close the dialog
            if (title.contains("Books")) addBook(tableModel);
            else if (title.contains("Borrow Records")) addBorrowRecord(tableModel);
            else if (title.contains("Librarians")) addLibrarian(tableModel);
            else if (title.contains("Customers")) addCustomer(tableModel);
        });

        removeButton.addActionListener(e -> {
            dialog.dispose(); // Close the dialog
            if (title.contains("Books")) removeBook(tableModel);
            else if (title.contains("Borrow Records")) removeBorrowRecord(tableModel);
            else if (title.contains("Librarians")) removeLibrarian(tableModel);
            else if (title.contains("Customers")) removeCustomer(tableModel);
        });

        backButton.addActionListener(e -> dialog.dispose()); // Close the dialog when Back is clicked

        // Display the dialog
        dialog.setLocationRelativeTo(null); // Center the dialog
        dialog.setVisible(true);
    }


    private static void executeQueryAndLoadData(String query, DefaultTableModel tableModel, String[] columnNames) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Clear existing data
            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);

            // Set column names
            for (String columnName : columnNames) {
                tableModel.addColumn(columnName);
            }

            // Add rows to the table model
            while (rs.next()) {
                Object[] rowData = new Object[columnNames.length];
                for (int i = 0; i < columnNames.length; i++) {
                    rowData[i] = rs.getObject(columnNames[i]);
                }
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void changeBookStatus(int bookID, DefaultTableModel tableModel, JTable table) {
        String[] statusOptions = {"Available", "Borrowed"}; // Status options
        String newStatus = (String) JOptionPane.showInputDialog(
                null,
                "Select the new status for the book:",
                "Change Book Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                statusOptions,
                statusOptions[0]);

        if (newStatus != null) { // User selected a status
            String query = "UPDATE Books SET Status = ? WHERE BookID = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, newStatus);
                pstmt.setInt(2, bookID);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Book status updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Update the table model directly
                    int selectedRow = table.getSelectedRow();
                    tableModel.setValueAt(newStatus, selectedRow, tableModel.findColumn("Status")); // Update the "Status" column
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to update book status!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error updating book status: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private static void addBook(DefaultTableModel tableModel) {
        String title = JOptionPane.showInputDialog("Enter Book Title:");
        String author = JOptionPane.showInputDialog("Enter Author:");
        String isbn = JOptionPane.showInputDialog("Enter ISBN:");
        String publisher = JOptionPane.showInputDialog("Enter Publisher:");
        String publishedYear = JOptionPane.showInputDialog("Enter Published Year:");
        String categoryId = JOptionPane.showInputDialog("Enter Category ID:");
        String status = JOptionPane.showInputDialog("Enter Status (Available/Borrowed):");

        if (title == null || title.trim().isEmpty() ||
                author == null || author.trim().isEmpty() ||
                isbn == null || isbn.trim().isEmpty() ||
                publishedYear == null || publishedYear.trim().isEmpty() ||
                categoryId == null || categoryId.trim().isEmpty() ||
                status == null || status.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "INSERT INTO Books (Title, Author, ISBN, Publisher, PublishedYear, CategoryID, Status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, isbn);
            pstmt.setString(4, publisher);
            pstmt.setString(5, publishedYear);
            pstmt.setString(6, categoryId);
            pstmt.setString(7, status);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            showBooksDialog();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding book: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void removeBook(DefaultTableModel tableModel) {
        String bookId = JOptionPane.showInputDialog("Enter Book ID to remove:");

        if (bookId == null || bookId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Book ID is required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "DELETE FROM Books WHERE BookID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, Integer.parseInt(bookId));

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Book removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Book not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }

            showBooksDialog();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error removing book: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void addBorrowRecord(DefaultTableModel tableModel) {
        String customerId = JOptionPane.showInputDialog("Enter Customer ID:");
        String bookId = JOptionPane.showInputDialog("Enter Book ID:");
        String borrowDate = JOptionPane.showInputDialog("Enter Borrow Date (YYYY-MM-DD):");
        String returnDate = JOptionPane.showInputDialog("Enter Return Date (YYYY-MM-DD):");
        String status = JOptionPane.showInputDialog("Enter Status (Borrowed/Returned/Overdue):");

        if (customerId == null || customerId.trim().isEmpty() ||
                bookId == null || bookId.trim().isEmpty() ||
                borrowDate == null || borrowDate.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Customer ID, Book ID, and Borrow Date are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "INSERT INTO BorrowRecords (CustomerID, BookID, BorrowDate, ReturnDate, Status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, customerId);
            pstmt.setString(2, bookId);
            pstmt.setString(3, borrowDate);
            pstmt.setString(4, returnDate);
            pstmt.setString(5, status);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Borrow record added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            showBorrowRecordsDialog();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding borrow record: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void removeBorrowRecord(DefaultTableModel tableModel) {
        String recordId = JOptionPane.showInputDialog("Enter Borrow Record ID to remove:");

        if (recordId == null || recordId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Borrow Record ID is required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "DELETE FROM BorrowRecords WHERE RecordID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, Integer.parseInt(recordId));

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Borrow record removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Borrow record not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }

            showBorrowRecordsDialog();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error removing borrow record: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void addLibrarian(DefaultTableModel tableModel) {
        String fullName = JOptionPane.showInputDialog("Enter Full Name:");
        String phoneNumber = JOptionPane.showInputDialog("Enter Phone Number:");
        String email = JOptionPane.showInputDialog("Enter Email:");
        String hireDate = JOptionPane.showInputDialog("Enter Hire Date (YYYY-MM-DD):");

        if (fullName == null || fullName.trim().isEmpty() ||
                phoneNumber == null || phoneNumber.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                hireDate == null || hireDate.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "INSERT INTO Librarians (FullName, PhoneNumber, Email, HireDate) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, fullName);
            pstmt.setString(2, phoneNumber);
            pstmt.setString(3, email);
            pstmt.setString(4, hireDate);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Librarian added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            showLibrariansDialog();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding librarian: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void removeLibrarian(DefaultTableModel tableModel) {
        String librarianID = JOptionPane.showInputDialog("Enter Librarian ID to remove:");

        if (librarianID == null || librarianID.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Librarian ID is required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "DELETE FROM Librarians WHERE LibrarianID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, Integer.parseInt(librarianID));

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Librarian removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Librarian not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }

            showLibrariansDialog();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error removing librarian: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void addCustomer(DefaultTableModel tableModel) {
        String fullName = JOptionPane.showInputDialog("Enter Full Name:");
        String address = JOptionPane.showInputDialog("Enter Address:");
        String phoneNumber = JOptionPane.showInputDialog("Enter Phone Number:");
        String email = JOptionPane.showInputDialog("Enter Email:");
        String membershipDate = JOptionPane.showInputDialog("Enter Membership Date (YYYY-MM-DD):");

        if (fullName == null || fullName.trim().isEmpty() ||
                address == null || address.trim().isEmpty() ||
                phoneNumber == null || phoneNumber.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                membershipDate == null || membershipDate.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "INSERT INTO Customer (FullName, Address, PhoneNumber, Email, MembershipDate) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, fullName);
            pstmt.setString(2, address);
            pstmt.setString(3, phoneNumber);
            pstmt.setString(4, email);
            pstmt.setString(5, membershipDate);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Customer added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            showCustomersDialog();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding customer: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void removeCustomer(DefaultTableModel tableModel) {
        String customerID = JOptionPane.showInputDialog("Enter Customer ID to remove:");

        if (customerID == null || customerID.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Customer ID is required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "DELETE FROM Customer WHERE CustomerID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, Integer.parseInt(customerID));

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Customer removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Customer not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }

            showCustomersDialog();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error removing customer: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
