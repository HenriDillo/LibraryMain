package librarymain;

// This class represents a librarian user
public class Librarian extends User {
    public Librarian(String username, String password) {
        super(username, password);
    }

    // Get the type of user
    @Override
    public String getUserType() {
        return "Librarian";
    }
}
