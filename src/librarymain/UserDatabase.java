package librarymain;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// This class manages the library's users
public class UserDatabase {
    private static UserDatabase instance;
    private List<User> users;
    private final String filePath = "users.txt";

    private UserDatabase() {
        users = new ArrayList<>();
        loadUsers();
    }

    public static UserDatabase getInstance() {
        if (instance == null) {
            instance = new UserDatabase();
        }
        return instance;
    }

    // Load users from the file
    private void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) { // Ensure there are at least three parts
                    String userType = parts[0].trim();
                    String username = parts[1].trim();
                    String password = parts[2].trim();
                    User user = UserFactory.createUser(userType, username, password);
                    if (user != null) {
                        users.add(user);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    // Save users to the file
    public void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (User user : users) {
                writer.write(user.getUserType() + "," + user.getUsername() + "," + user.getPassword());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    // Get all users in the library
    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    // Check if a user exists
    public boolean userExists(String userType, String username) {
        for (User user : users) {
            if (user.getUserType().equalsIgnoreCase(userType) &&
                user.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    // Add a new user to the library
    public boolean addUser(User user) {
        if (userExists(user.getUserType(), user.getUsername())) {
            return false; // User already exists
        }
        users.add(user);
        saveUsers();
        return true; // User added successfully
    }
} 