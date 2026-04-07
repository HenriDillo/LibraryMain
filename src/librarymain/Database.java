package librarymain;

import java.util.List;
import java.util.Optional;

// This class manages the library's users and books
public class Database {
    private static Database instance;
    private UserDatabase userDatabase;
    private BookDatabase bookDatabase;

    private Database() {
        userDatabase = UserDatabase.getInstance();
        bookDatabase = BookDatabase.getInstance();
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    // Get all users in the library
    public List<User> getAllUsers() {
        return userDatabase.getUsers();
    }

    // Add a new user to the library
    public boolean addUser(String userType, String username, String password) {
        User user = UserFactory.createUser(userType, username, password);
        if (user != null) {
            return userDatabase.addUser(user);
        }
        return false;
    }

    // Add a new book to the library
    public void addBook(Book book) {
        bookDatabase.addBook(book);
    }

    // Remove a book from the library
    public void removeBook(String title) {
        bookDatabase.removeBook(title);
    }

    // Search for a book by title
    public Optional<Book> searchBook(String title) {
        return bookDatabase.getBooks().stream()
            .filter(book -> book.getDescription().contains(title))
            .findFirst();
    }

    // Authenticate a user by username and password
    public User authenticateUser(String username, String password, String userType) {
        return userDatabase.getUsers().stream()
            .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
            .findFirst()
            .orElse(null);
    }

    // Get all books in the library
    public List<Book> getBooks() {
        return bookDatabase.getBooks();
    }

    // Add a user to the library
    public boolean addUser(User user) {
        return addUser(user.getUserType(), user.getUsername(), user.getPassword());
    }

    // Check if a book is a duplicate
    public boolean isDuplicateBook(String title, String author) {
        return bookDatabase.getBooks().stream()
            .anyMatch(book -> book.getDescription().equalsIgnoreCase(title) 
                && book.getAuthor().equalsIgnoreCase(author));
    }

    public void removeBookFromFile(String title) {
        bookDatabase.removeBookFromFile(title);
    }

    public void saveBookToFile(String title, String author, String bookType) {
        bookDatabase.saveBookToFile(title, author, bookType);
    }

    public String getBookTypeFromFile(String title) {
        return bookDatabase.getBookTypeFromFile(title);
    }
}
