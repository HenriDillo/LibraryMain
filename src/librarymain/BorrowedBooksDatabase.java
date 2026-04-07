package librarymain;

import java.io.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

public class BorrowedBooksDatabase {
    private static BorrowedBooksDatabase instance;
    private final String filePath = "borrowed_books.txt"; // File to store borrowed books
    private Map<String, BufferedWriter> userWriters;

    private BorrowedBooksDatabase() {
        userWriters = new HashMap<>();
    }

    public static BorrowedBooksDatabase getInstance() {
        if (instance == null) {
            instance = new BorrowedBooksDatabase();
        }
        return instance;
    }

    // This method logs when a book is borrowed
    public void logBorrowedBook(Borrower borrower, Book book) {
        String username = borrower.getUsername();
        LocalDate dateBorrowed = LocalDate.now(); // Today's date
        LocalDate dueDate = dateBorrowed.plusDays(3); // Due date is 3 days from now

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            // Write the borrower's name, book description, and dates to the file
            writer.write(String.format("%s, %s, %s, %s%n", 
                username, 
                book.getDescription(), 
                dateBorrowed, 
                dueDate));
            writer.flush(); // Make sure the information is saved
        } catch (IOException e) {
            System.err.println("Error writing to user file: " + e.getMessage());
        }
    }

    // This method closes all file writers
    public void closeAllWriters() {
        for (BufferedWriter writer : userWriters.values()) {
            try {
                writer.close();
            } catch (IOException e) {
                System.err.println("Error closing user file: " + e.getMessage());
            }
        }
    }

    // This method removes a borrowed book from the file
    public void removeBorrowedBook(String username, String bookTitle) {
        File inputFile = new File(filePath);
        File tempFile = new File("temp_borrowed_books.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                // If the line matches the book to remove, skip it
                if (parts.length == 4 && parts[0].equals(username) && parts[1].equals(bookTitle)) {
                    continue;
                }
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error removing borrowed book: " + e.getMessage());
        }

        // Replace the old file with the new one
        if (inputFile.delete()) {
            tempFile.renameTo(inputFile);
        }
    }

    // This method loads borrowed books data into the table
    public void loadBorrowedBooksData(DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // Clear existing rows
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String currentUsername = null;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length == 4) {
                    String username = parts[0];
                    String bookTitle = parts[1];
                    String dateBorrowed = parts[2];
                    String dueDate = parts[3];
                    
                    if (!username.equals(currentUsername)) {
                        tableModel.addRow(new Object[]{username, bookTitle, dateBorrowed, dueDate});
                        currentUsername = username;
                    } else {
                        tableModel.addRow(new Object[]{"", bookTitle, dateBorrowed, dueDate});
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading borrowed books: " + e.getMessage());
        }
    }

    // This method searches for a borrower in the borrowed books records
    public void searchBorrower(DefaultTableModel tableModel, String searchTerm) {
        tableModel.setRowCount(0);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String currentUsername = null;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length == 4) {
                    String username = parts[0];
                    String bookTitle = parts[1];
                    String dateBorrowed = parts[2];
                    String dueDate = parts[3];
                    
                    if (username.equalsIgnoreCase(searchTerm)) {
                        if (!username.equals(currentUsername)) {
                            tableModel.addRow(new Object[]{username, bookTitle, dateBorrowed, dueDate});
                            currentUsername = username;
                        } else {
                            tableModel.addRow(new Object[]{"", bookTitle, dateBorrowed, dueDate});
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error searching borrowed books: " + e.getMessage());
        }
    }

    // Check if a book is currently borrowed
    public boolean isBookBorrowed(String bookTitle) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length >= 2 && parts[1].equalsIgnoreCase(bookTitle)) {
                    return true; // Book is found in borrowed records
                }
            }
        } catch (IOException e) {
            System.err.println("Error checking borrowed books: " + e.getMessage());
        }
        return false; // Book is not borrowed
    }
} 