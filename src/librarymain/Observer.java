package librarymain;

// This interface is for objects that want to receive updates
public interface Observer {
    void update(String message); // Method to receive a message
}
