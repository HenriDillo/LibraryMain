package librarymain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Borrower extends User implements Observer {
    private List<BorrowingRecord> borrowingRecords = new ArrayList<>();

    public Borrower(String username, String password) {
        super(username, password);
        NotificationSystem.getInstance().addObserver(this);
    }

    @Override
    public void update(String message) {
        // Remove terminal output, let GUI handle all notifications
        // Only keep the message filtering
        if (message.contains(getUsername())) {
            // Do nothing here - GUI will handle the display
        }
    }

    public void checkOutBook(Book book) {
        BorrowingRecord record = new BorrowingRecord(book);
        borrowingRecords.add(record);
        NotificationSystem.getInstance().notifyObservers(
            String.format("Book '%s' has been checked out by %s", 
                book.getDescription(), getUsername()));
    }

    public boolean returnBook(Book book) {
        for (BorrowingRecord record : borrowingRecords) {
            if (record.getBook().equals(book) && !record.isReturned()) {
                record.returnBook();
                NotificationSystem.getInstance().notifyObservers(
                    String.format("Book '%s' has been returned by %s", 
                        book.getDescription(), getUsername()));
                return true;
            }
        }
        return false;
    }

    public String viewOverdueBooks() {
        List<BorrowingRecord> overdueBooks = borrowingRecords.stream()
                .filter(record -> record.isOverdue())
                .collect(Collectors.toList());
        
        if (overdueBooks.isEmpty()) {
            return "No overdue books.";
        }

        StringBuilder result = new StringBuilder("Overdue Books:\n");
        for (BorrowingRecord record : overdueBooks) {
            result.append(record.toString()).append("\n");
        }
        return result.toString();
    }

    public List<BorrowingRecord> getBorrowingRecords() {
        return new ArrayList<>(borrowingRecords);
    }

    @Override
    public String getUserType() {
        return "Borrower";
    }

    

    
}
