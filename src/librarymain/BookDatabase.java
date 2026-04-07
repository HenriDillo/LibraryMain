package librarymain;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// This class keeps track of all books in the library (like a card catalog)
public class BookDatabase {
    private static BookDatabase instance;  // Only one copy of the database exists
    private List<Book> books;  // List to store all our books
    private final String filePath = "books.txt";  // File where we save book information

    // Private constructor - we only want one database
    private BookDatabase() {
        books = new ArrayList<>();
        loadBooks();  // Load books when starting up
    }

    // Get the one and only copy of the database
    public static BookDatabase getInstance() {
        if (instance == null) {
            instance = new BookDatabase();
        }
        return instance;
    }

    // Read all books from our file and add them to our list
    private void loadBooks() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");  // Each line has title, author
                String title = parts[0].trim();
                String author = parts[1].trim();
                books.add(new BasicBook(title, author));  // Create and add the book
            }
        } catch (IOException e) {
            System.err.println("Error loading books: " + e.getMessage());
        }
    }

    // Check if we already have this book
    public boolean isDuplicateBook(String title, String author) {
        for (Book book : books) {
            // Compare ignoring upper/lower case
            if (book.getDescription().equalsIgnoreCase(title) && 
                book.getAuthor().equalsIgnoreCase(author)) {
                return true;  // Found a match
            }
        }
        return false;  // No match found
    }

    // Get a copy of our book list
    public List<Book> getBooks() {
        return new ArrayList<>(books);  // Return a copy so original list stays safe
    }

    // Add a new book to our collection
    public void addBook(Book book) {
        books.add(book);
    }

    // Remove a book from our collection
    public void removeBook(String title) {
        books.removeIf(book -> book.getDescription().equalsIgnoreCase(title));
    }

    // Write a new book to our file
    public void saveBookToFile(String title, String author, String bookType) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            // Add the book info on a new line
            writer.write(String.format("%s, %s, %s%n", title, author, bookType));
        } catch (IOException e) {
            System.err.println("Error saving book to file: " + e.getMessage());
        }
    }

    // Remove a book from our file
    public void removeBookFromFile(String title) {
        File inputFile = new File(filePath);
        File tempFile = new File("temp_books.txt");  // Temporary file for our changes

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                // Skip the book we want to remove
                if (parts.length >= 3 && parts[0].equalsIgnoreCase(title)) {
                    continue;
                }
                writer.write(line);  // Write all other books
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error removing book from file: " + e.getMessage());
        }

        // Replace old file with new one
        if (inputFile.delete()) {
            tempFile.renameTo(inputFile);
        }
    }

    // Find out what type of book it is (Regular, Bestseller, E-Book)
    public String getBookTypeFromFile(String title) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length >= 3 && parts[0].equalsIgnoreCase(title)) {
                    return parts[2].trim();  // Return the book type
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading book types: " + e.getMessage());
        }
        return null;  // Book not found
    }
} 