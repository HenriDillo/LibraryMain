package librarymain;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// This class creates a window for librarians to manage books and borrowers
public class LibrarianGUI extends JFrame {
    private JTextField bookTitleField;
    private JTextField authorField;
    private JComboBox<String> bookTypeCombo;
    private Database database;
    private JTable bookTable;
    
    private JTabbedPane tabbedPane;
    private DefaultTableModel bookTableModel;
    private DefaultTableModel borrowerTableModel;

    public LibrarianGUI(Database database) {
        this.database = database;
        setupFrame();
        setupTabbedPane();
    }

    // Set up the main window
    private void setupFrame() {
        setTitle("Librarian - Library Management");
        setSize(1000, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UiTheme.BACKGROUND);
    }

    // Set up the tabs for different management tasks
    private void setupTabbedPane() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UiTheme.FONT_BUTTON);
        tabbedPane.addTab("Book Management", createBookPanel());
        tabbedPane.addTab("Borrower Management", createBorrowerPanel());
        JPanel container = UiTheme.createPanel(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        container.add(tabbedPane, BorderLayout.CENTER);
        add(container);
    }

    // Create the panel for managing books
    private JPanel createBookPanel() {
        JPanel panel = UiTheme.createPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        // Create a panel for input fields
        JPanel inputPanel = UiTheme.createPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(UiTheme.cardBorder());
    
        JLabel titleLabel = new JLabel("Book Title");
        titleLabel.setForeground(UiTheme.TEXT_PRIMARY);
        inputPanel.add(titleLabel);
        bookTitleField = new JTextField();
        UiTheme.styleInput(bookTitleField);
        inputPanel.add(bookTitleField);
    
        JLabel authorLabel = new JLabel("Author");
        authorLabel.setForeground(UiTheme.TEXT_PRIMARY);
        inputPanel.add(authorLabel);
        authorField = new JTextField();
        UiTheme.styleInput(authorField);
        inputPanel.add(authorField);
    
        JLabel typeLabel = new JLabel("Book Type");
        typeLabel.setForeground(UiTheme.TEXT_PRIMARY);
        inputPanel.add(typeLabel);
        bookTypeCombo = new JComboBox<>(new String[]{"Regular", "Bestseller", "E-Book"});
        bookTypeCombo.setFont(UiTheme.FONT_BODY);
        bookTypeCombo.setBackground(Color.WHITE);
        inputPanel.add(bookTypeCombo);
    
        JPanel bookButtonPanel = UiTheme.createPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        addBookButtons(bookButtonPanel);
    
        String[] columnNames = {"Title", "Author"};
        bookTableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(bookTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make cells non-editable
            }
        };
        
        // Load books from the database
        loadBooks(bookTableModel);
    
        JScrollPane tableScrollPane = new JScrollPane(bookTable);
        UiTheme.styleTable(bookTable, tableScrollPane);
    
        JPanel centerPanel = UiTheme.createPanel(new BorderLayout(8, 8));
        centerPanel.add(bookButtonPanel, BorderLayout.NORTH);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
    
        // Add components to the main panel
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER); // Use the center panel to hold buttons and table
    
        return panel;
    }

    // Add buttons for book actions
    private void addBookButtons(JPanel buttonPanel) {
        JButton addButton = new JButton("Add Book");
        JButton deleteButton = new JButton("Delete Book");
        JButton searchButton = new JButton("Search Book");
        JButton backButton = new JButton("Back");
        JButton resetButton = new JButton("Reset Table");
        UiTheme.styleButton(addButton, true);
        UiTheme.styleButton(deleteButton, false);
        UiTheme.styleButton(searchButton, false);
        UiTheme.styleButton(backButton, false);
        UiTheme.styleButton(resetButton, false);

        // Create the filter combo box
        String[] filterOptions = {"All", "Regular", "Bestseller", "E-Book"};
        JComboBox<String> filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.setFont(UiTheme.FONT_BODY);
        filterComboBox.setBackground(Color.WHITE);
        filterComboBox.addActionListener(e -> filterBooks((String) filterComboBox.getSelectedItem()));
        
        
        buttonPanel.add(filterComboBox); 

        // Action listener for the Add Book button
        addButton.addActionListener(e -> addBook());
        buttonPanel.add(addButton);

        // Action listener for the Delete Book button
        deleteButton.addActionListener(e -> deleteBook());
        buttonPanel.add(deleteButton);

        // Action listener for the Search Book button
        searchButton.addActionListener(e -> searchBook());
        buttonPanel.add(searchButton);

        // Action listener for the Back button
        backButton.addActionListener(e -> goBack());
        buttonPanel.add(backButton);

        // Action listener for the Reset Table button
        resetButton.addActionListener(e -> resetTable());
        buttonPanel.add(resetButton);
    }

    // Add a new book to the system
    private void addBook() {
        try {
            String title = bookTitleField.getText().trim();
            String author = authorField.getText().trim();
            String bookType = (String) bookTypeCombo.getSelectedItem();

            if (title.isEmpty() || author.isEmpty() || bookType == null) {
                throw new IllegalArgumentException("Please fill in all fields");
            }

            // Check for duplicate book
            if (database.isDuplicateBook(title, author)) {
                JOptionPane.showMessageDialog(this, "Book already exists", "Duplicate Book", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Book book = new BasicBook(title, author);
            
             switch (bookType) {
                    case "Bestseller":
                        book = new BestsellerDecorator(book);
                        break;
                    case "E-Book":
                        book = new EBookDecorator(book);
                        break;
                }
            database.addBook(book);

            // Save the book to the text file
            database.saveBookToFile(title, author, bookType);
            
            // Add success message
            JOptionPane.showMessageDialog(this, 
                "Book added successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
                
            clearFields();
            loadBooks(bookTableModel);
        } catch (Exception ex) {
            showError("Error adding book", ex.getMessage());
        }
    }

    // Delete a book from the system
    private void deleteBook() {
        try {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow == -1) {
                throw new IllegalArgumentException("Please select a book to delete");
            }

            String title = (String) bookTableModel.getValueAt(selectedRow, 0);
            // Check if the book is currently borrowed
            if (BorrowedBooksDatabase.getInstance().isBookBorrowed(title)) {
                JOptionPane.showMessageDialog(this, 
                    "Book is currently borrowed and cannot be deleted", 
                    "Cannot Delete", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            database.removeBook(title); // Remove from database

            // Remove from books.txt
            database.removeBookFromFile(title);

            // Add success message
            JOptionPane.showMessageDialog(this, 
                "Book deleted successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);

            loadBooks(bookTableModel); // Update the book table after deleting
        } catch (Exception ex) {
            showError("Error deleting book", ex.getMessage());
        }
    }

    // Search for a book in the system
    private void searchBook() {
        try {
            String titleSearchTerm = bookTitleField.getText().trim(); // Search term for title
            String authorSearchTerm = authorField.getText().trim(); // Search term for author

            // Clear the existing rows in the table model
            bookTableModel.setRowCount(0); // Clear existing entries

            // Check if both fields are empty
            if (titleSearchTerm.isEmpty() && authorSearchTerm.isEmpty()) {
                throw new IllegalArgumentException("Please enter a book title or author to search");
            }

            // Search for the book in the database
            List<Book> books = database.getBooks(); // Get all books from the database
            boolean found = false; // Flag to check if any book is found

            for (Book book : books) {
                boolean matchesTitle = !titleSearchTerm.isEmpty() && 
                                       book.getDescription().toLowerCase().contains(titleSearchTerm.toLowerCase());
                boolean matchesAuthor = !authorSearchTerm.isEmpty() && 
                                        book.getAuthor().toLowerCase().contains(authorSearchTerm.toLowerCase());

                // If a match is found in either title or author, add it to the table
                if (matchesTitle || matchesAuthor) {
                    bookTableModel.addRow(new Object[]{book.getDescription(), book.getAuthor()});
                    found = true;
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(this,
                    "No books found for the given search input.",
                    "Search Results",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            showError("Error searching book", ex.getMessage());
        }
    }

    // Load books into the table
    private void loadBooks(DefaultTableModel bookTableModel) {
        // Clear existing rows in the table model to avoid redundancy
        bookTableModel.setRowCount(0); // Clear existing entries

        List<Book> books = database.getBooks();
        for (Book book : books) {
            // Get the original title without the decorator
            String originalTitle = book.getDescription();
            // Add the book to the table without the decorator
            bookTableModel.addRow(new Object[]{originalTitle, book.getAuthor()});
        }
    }

    // Show an error message
    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    // Clear input fields
    private void clearFields() {
        bookTitleField.setText("");
        authorField.setText("");
        bookTypeCombo.setSelectedIndex(0); // Reset the combo box to the first item
    }

    // Create the panel for managing borrowers
    private JPanel createBorrowerPanel() {
        JPanel panel = UiTheme.createPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        // Create a panel for search functionality
        JPanel searchPanel = UiTheme.createPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JTextField searchField = new JTextField(15); // Search field
        JButton searchButton = new JButton("Search"); // Search button
        JButton resetButton = new JButton("Reset Table"); // Reset button
        UiTheme.styleInput(searchField);
        UiTheme.styleButton(searchButton, true);
        UiTheme.styleButton(resetButton, false);
        
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim();
            searchBorrower(borrowerTableModel, searchTerm);
        });
        
        resetButton.addActionListener(e -> {
            // Clear the current search results
            borrowerTableModel.setRowCount(0); // Clear existing entries in the table model
            loadBorrowerData(borrowerTableModel); // Reload the original list of borrowers
            searchField.setText(""); // Clear the search field
        });
        
        JLabel searchLabel = new JLabel("Search User:");
        searchLabel.setForeground(UiTheme.TEXT_PRIMARY);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton); // Add reset button to the search panel
        panel.add(searchPanel, BorderLayout.NORTH); // Add search panel to the top
        
        // Create a table to display borrowers and their borrowed books
        String[] columnNames = {"Username", "Book Title", "Date Borrowed", "Due Date"};
        borrowerTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make cells non-editable
            }
        };
        
        JTable borrowerTable = new JTable(borrowerTableModel);
        borrowerTable.getTableHeader().setReorderingAllowed(false); // Prevent column reordering
        borrowerTable.setFillsViewportHeight(true); // Fill the viewport
        
        // Load data into the table initially
        loadBorrowerData(borrowerTableModel);
        
        JScrollPane tableScrollPane = new JScrollPane(borrowerTable);
        UiTheme.styleTable(borrowerTable, tableScrollPane);
        panel.add(tableScrollPane, BorderLayout.CENTER); // Add the table to the panel
        
        return panel;
    }

    // Load borrower data into the table
    private void loadBorrowerData(DefaultTableModel borrowerTableModel) {
        BorrowedBooksDatabase.getInstance().loadBorrowedBooksData(borrowerTableModel);
    }

    // Search for a borrower in the system
    private void searchBorrower(DefaultTableModel borrowerTableModel, String searchTerm) {
        BorrowedBooksDatabase.getInstance().searchBorrower(borrowerTableModel, searchTerm);
    }

    // Go back to the previous screen
    private void goBack() {
        dispose(); // Close the current GUI
        new LibraryGUI().setVisible(true); // Open the main library GUI
    }

    // Reset the book table to its original state
    private void resetTable() {
        loadBooks(bookTableModel); // Reload the original list of books
    }

    // Filter books by type
    private void filterBooks(String type) {
        // Clear existing rows in the table model
        bookTableModel.setRowCount(0);
        
        // Load books from the database and filter by type
        List<Book> books = database.getBooks();
        for (Book book : books) {
            String bookType = database.getBookTypeFromFile(book.getDescription());
            if (type.equals("All") || 
                (bookType != null && bookType.equals(type))) {
                
                bookTableModel.addRow(new Object[]{book.getDescription(), book.getAuthor()}); // Only add title and author
            }
        }
    }
}
