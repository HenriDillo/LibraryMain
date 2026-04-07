package librarymain;

import java.util.Timer;
import java.util.TimerTask;

// This is the main class that starts the library system
public class LibraryMain {
    public static void main(String[] args) {
        NotificationSystem notificationSystem = NotificationSystem.getInstance();

        // Start periodic overdue book checker
        Timer overdueChecker = new Timer();
        overdueChecker.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                notificationSystem.checkOverdueBooks();
            }
        }, 0, 24 * 60 * 60 * 1000); // Check every 24 hours

        // Start GUI
        javax.swing.SwingUtilities.invokeLater(() -> {
            UiTheme.installLookAndFeel();
            LibraryGUI gui = new LibraryGUI();
            gui.setVisible(true);
        });

        // Add shutdown hook to close user file writers
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            BorrowedBooksDatabase.getInstance().closeAllWriters();
        }));
    }
}
