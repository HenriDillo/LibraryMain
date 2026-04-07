package librarymain;

// This class represents a user in the library
public abstract class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Get the user's username
    public String getUsername() {
        return username;
    }

    // Get the user's password
    public String getPassword() {
        return password;
    }

    // Check if two users are the same
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return username.equals(user.username);
    }

    // Get a unique number for the user
    @Override
    public int hashCode() {
        return username.hashCode();
    }

    // Get the type of user
    public abstract String getUserType();
}
