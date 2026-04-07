package librarymain;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class BorrowerGUI extends JFrame implements Observer {
    private JTextField searchField;
    private Database database;
    private Borrower borrower;
    private DefaultTableModel bookTableModel;
    private JTable bookTable;
    private DefaultTableModel borrowedBooksTableModel;
    private JTable borrowedBooksTable;
    private BorrowedBooksDatabase userFileManager;

    public BorrowerGUI(Database database, Borrower borrower) {
        this.database = database;
        this.borrower = borrower;
        this.userFileManager = BorrowedBooksDatabase.getInstance();
        NotificationSystem.getInstance().addObserver(this); // Register as observer
        
        setupFrame();
        setupComponents();
        
        // Show notifications after GUI setup
        SwingUtilities.invokeLater(() -> {
            // Check for overdue books
            NotificationSystem.getInstance().checkOverdueBooks();
            
            // Show new book arrivals in a single message
            List<Book> recentBooks = database.getBooks();
            // Get the last 3 books (or fewer if less exist)
            int startIndex = Math.max(0, recentBooks.size() - 3);
            if (startIndex < recentBooks.size()) {
                StringBuilder newBooksMessage = new StringBuilder("NEW ARRIVALS!!:\n\n");
                for (int i = startIndex; i < recentBooks.size(); i++) {
                    Book book = recentBooks.get(i);
                    newBooksMessage.append("• ").append(book.getDescription())
                                  .append(" by ").append(book.getAuthor()).append("\n");
                }
                JOptionPane.showMessageDialog(this,
                    newBooksMessage.toString(),
                    "New Books Available",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    @Override
    public void update(String message) {
        SwingUtilities.invokeLater(() -> {
            // Show popup for new book arrivals only
            if (message.startsWith("NEW BOOK ARRIVAL:")) {
                JOptionPane.showMessageDialog(this,
                    message,
                    "New Book Arrival",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
            // Log overdue notifications in the operations log
            if (message.startsWith(borrower.getUsername() + ": OVERDUE:")) {
                String overdueMessage = message.substring(message.indexOf(": ") + 2); // Remove username prefix
                // Show popup
                JOptionPane.showMessageDialog(this,
                    overdueMessage,
                    "Overdue Book Notice",
                    JOptionPane.WARNING_MESSAGE);
            }
            // Show popup and log for due date reminders
            else if (message.startsWith(borrower.getUsername() + ": REMINDER:")) {
                String reminderMessage = message.substring(message.indexOf(": ") + 2); // Remove username prefix
                // Show popup
                JOptionPane.showMessageDialog(this,
                    reminderMessage,
                    "Due Date Reminder",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    // When closing the GUI, remove the observer
    @Override
    public void dispose() {
        NotificationSystem.getInstance().removeObserver(this);
        super.dispose();
    }

    private void setupFrame() {
        setTitle("Borrower - Library Management");
        setSize(900, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));
        getContentPane().setBackground(UiTheme.BACKGROUND);
    }

    private void setupComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UiTheme.FONT_BUTTON);
        tabbedPane.setBackground(UiTheme.PANEL_BACKGROUND);

        JPanel borrowedBooksTab = createBorrowedBooksPanel();
        tabbedPane.addTab("Borrowed Books", borrowedBooksTab);

        JPanel bookListTab = createBookListPanel();
        tabbedPane.addTab("Search/Borrow Books", bookListTab);

        JPanel container = UiTheme.createPanel(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        container.add(tabbedPane, BorderLayout.CENTER);
        add(container, BorderLayout.CENTER);
    }

    private JPanel createBookListPanel() {
        JPanel panel = UiTheme.createPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel searchPanel = UiTheme.createPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchField = new JTextField(17);
        UiTheme.styleInput(searchField);
        JButton searchButton = new JButton("Search");
        JButton resetButton = new JButton("Reset Table");
        JButton borrowButton = new JButton("Borrow");
        UiTheme.styleButton(searchButton, true);
        UiTheme.styleButton(resetButton, false);
        UiTheme.styleButton(borrowButton, false);

        // Action listener for the search button
        searchButton.addActionListener(e -> searchBook(searchField.getText().trim()));
        JLabel searchLabel = new JLabel("Search (Title/Author):");
        searchLabel.setForeground(UiTheme.TEXT_PRIMARY);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Action listener for the reset button
        resetButton.addActionListener(e -> resetTable());
        searchPanel.add(resetButton);
        
        // Action listener for the borrow button
        borrowButton.addActionListener(e -> borrowSelectedBook());
        searchPanel.add(borrowButton);

        // Create a table to display books with an additional "Status" column
        String[] columnNames = {"Title", "Author", "Status"};
        bookTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make the table non-editable
            }
        };
        bookTable = new JTable(bookTableModel);
        bookTable.getTableHeader().setReorderingAllowed(false); // Prevent column reordering
        JScrollPane tableScrollPane = new JScrollPane(bookTable);
        UiTheme.styleTable(bookTable, tableScrollPane);

        // Load books from the database
        loadBooks(bookTableModel);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBorrowedBooksPanel() {
        JPanel panel = UiTheme.createPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        // Create a table to display borrowed books
        String[] columnNames = {"Books Borrowed", "Date Borrowed", "Due Date"};
        borrowedBooksTableModel = new DefaultTableModel(columnNames, 0);
        borrowedBooksTable = new JTable(borrowedBooksTableModel);
        borrowedBooksTable.getTableHeader().setReorderingAllowed(false); // Prevent column reordering
        
        // Load borrowed books from the file
        loadBorrowedBooks(borrowedBooksTableModel);
        
        JScrollPane scrollPane = new JScrollPane(borrowedBooksTable);
        UiTheme.styleTable(borrowedBooksTable, scrollPane);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(UiTheme.BORDER),
            "Borrowed Books Details"
        ));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Create a panel for buttons
        JPanel buttonPanel = UiTheme.createPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton returnBookButton = new JButton("Return Book");
        JButton backButton = new JButton("Back");
        UiTheme.styleButton(returnBookButton, true);
        UiTheme.styleButton(backButton, false);

        // Action listener for the return book button
        returnBookButton.addActionListener(e -> returnSelectedBook());
        buttonPanel.add(returnBookButton);
        
        // Action listener for the back button
        backButton.addActionListener(e -> goBack());
        buttonPanel.add(backButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH); // Add button panel to the bottom
        
        return panel;
    }

    private void searchBook(String query) {
        try {
            // Clear the existing rows in the table model
            bookTableModel.setRowCount(0);

            // Check if the search term is empty
            if (query.isEmpty()) {
                throw new IllegalArgumentException("Please enter a book title or author to search");
            }

            // Search for the book in the database
            List<Book> books = database.getBooks();
            boolean found = false;

            for (Book book : books) {
                boolean matchesTitle = book.getDescription().toLowerCase().contains(query.toLowerCase());
                boolean matchesAuthor = book.getAuthor().toLowerCase().contains(query.toLowerCase());

                if (matchesTitle || matchesAuthor) {
                    String status = isBookBorrowed(book) ? "Borrowed" : "Available";
                    bookTableModel.addRow(new Object[]{book.getDescription(), book.getAuthor(), status});
                    found = true;
                }
            }

            if (found) {
                // Remove: logOperation("Search found results for: " + query);
            } else {
                // Remove: logOperation("No books found for: " + query);
            }
        } catch (Exception ex) {
            showError("Error searching book", ex.getMessage());
        }
    }

    private void resetTable() {
        bookTableModel.setRowCount(0); // Clear the current table
        loadBooks(bookTableModel); // Reload the original list of books
    }

    private void borrowSelectedBook() {
        // Get the selected row from the table
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Error", "Please select a book to borrow.");
            return;
        }

        String title = (String) bookTableModel.getValueAt(selectedRow, 0); // Get the title from the selected row
        Optional<Book> bookOpt = database.searchBook(title);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            
            // Check if the book is already borrowed
            if (isBookBorrowed(book)) {
                showError("Error", "This book is already borrowed by another user.");
                return; // Prevent borrowing if already borrowed
            }

            userFileManager.logBorrowedBook(borrower, book);
            
            // Update the status in the table model
            bookTableModel.setValueAt("Borrowed", selectedRow, 2); // Update the status column
            
            // Refresh both tables to reflect the change
            refreshBookTable();
            refreshBorrowedBooksTable(); // Update the borrowed books table
            
            // Show success message
            JOptionPane.showMessageDialog(this, "Successfully borrowed the book: " + book.getDescription(), "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            showError("Error", "Book not found.");
        }
    }

    private void returnSelectedBook() {
        // Get the selected row from the borrowed books table
        int selectedRow = borrowedBooksTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Error", "Please select a book to return.");
            return;
        }

        String title = (String) borrowedBooksTableModel.getValueAt(selectedRow, 0); // Get the title from the selected row

        // Remove the book from the borrowed_books.txt
        userFileManager.removeBorrowedBook(borrower.getUsername(), title);
        
        // Refresh the borrowed books table
        refreshBorrowedBooksTable();

        // Update the available books table
        refreshBookTable();

        // Show success message
        JOptionPane.showMessageDialog(this, "Successfully returned the book: " + title, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshBookTable() {
        bookTableModel.setRowCount(0); // Clear the current table
        loadBooks(bookTableModel); // Reload the original list of books
    }

    private void refreshBorrowedBooksTable() {
        borrowedBooksTableModel.setRowCount(0); // Clear the current borrowed books table
        loadBorrowedBooks(borrowedBooksTableModel); // Reload the borrowed books from the file
    }

    private void loadBooks(DefaultTableModel bookTableModel) {
        // Fetching the list of books from the database
        List<Book> books = database.getBooks();
        for (Book book : books) {
            String status = isBookBorrowed(book) ? "Borrowed" : "Available";
            bookTableModel.addRow(new Object[]{book.getDescription(), book.getAuthor(), status});
        }
    }

    private boolean isBookBorrowed(Book book) {
        // Check if the book is borrowed by reading from the borrowed_books.txt file
        try (BufferedReader reader = new BufferedReader(new FileReader("borrowed_books.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length == 4 && parts[1].equals(book.getDescription())) {
                    return true; // Book is found in the borrowed list
                }
            }
        } catch (IOException e) {
            showError("Error checking borrowed status", e.getMessage());
        }
        return false; // Book is not borrowed
    }

    private void loadBorrowedBooks(DefaultTableModel borrowedBooksTableModel) {
        try (BufferedReader reader = new BufferedReader(new FileReader("borrowed_books.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length == 4 && parts[0].equals(borrower.getUsername())) {  // Check if book belongs to current borrower
                    // Only add the book title, date borrowed, and due date to the table
                    String[] rowData = {parts[1], parts[2], parts[3]}; // Exclude username (parts[0])
                    borrowedBooksTableModel.addRow(rowData);
                }
            }
        } catch (IOException e) {
            showError("Error loading borrowed books", e.getMessage());
        }
    }

    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void goBack() {
        dispose();
        new LibraryGUI().setVisible(true);
    }

}
