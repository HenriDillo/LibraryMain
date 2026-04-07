package librarymain;

// This class creates users based on their type
public class UserFactory {
    public static User createUser(String userType, String username, String password) {
        switch (userType) {
            case "Librarian":
                return new Librarian(username, password);
            case "Borrower":
                return new Borrower(username, password);
            default:
                return null;
        }
    }
}
