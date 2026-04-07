package librarymain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// This class handles sending notifications to users
public class NotificationSystem {
    private static NotificationSystem instance;
    private List<Observer> observers = new ArrayList<>();

    private NotificationSystem() {}

    public static NotificationSystem getInstance() {
        if (instance == null) {
            instance = new NotificationSystem();
        }
        return instance;
    }

    // Add a new observer to the list
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    // Notify all observers with a message
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }

    // Check for books that are overdue
    public void checkOverdueBooks() {
        try (BufferedReader reader = new BufferedReader(new FileReader("borrowed_books.txt"))) {
            String line;
            LocalDate today = LocalDate.now();
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(", ");
                if (parts.length >= 4) {
                    String username = parts[0];
                    String bookTitle = parts[1];
                    LocalDate dueDate = LocalDate.parse(parts[3]);
                    
                    if (today.isAfter(dueDate)) {
                        // Notify if the book is overdue
                        String message = "OVERDUE: The book '" + bookTitle + "' was due on " + 
                                       dueDate + ". Please return it as soon as possible.";
                        notifyObservers(username + ": " + message);
                    } else {
                        // Remind if the book is due soon
                        if (dueDate.minusDays(2).isBefore(today)) {
                            String message = "REMINDER: The book '" + bookTitle + "' is due on " + 
                                           dueDate + ".";
                            notifyObservers(username + ": " + message);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Remove an observer from the list
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    // Notify observers about a new book arrival
    public void notifyNewBookArrival(Book book) {
        String message = String.format("NEW BOOK ARRIVAL: '%s' by %s is now available!", 
            book.getDescription(), 
            book.getAuthor());
        notifyObservers(message);
    }
} 