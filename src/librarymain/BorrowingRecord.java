package librarymain;

import java.time.LocalDate;

// This class keeps track of a borrowed book
public class BorrowingRecord {
    private Book book;
    private LocalDate dueDate;
    private boolean returned;

    public BorrowingRecord(Book book) {
        this.book = book;
        this.dueDate = LocalDate.now().plusDays(3); // Due date is 3 days from now
        this.returned = false;
    }

    // Get the book
    public Book getBook() {
        return book;
    }

    // Get the due date
    public LocalDate getDueDate() {
        return dueDate;
    }

    // Check if the book is returned
    public boolean isReturned() {
        return returned;
    }

    // Mark the book as returned
    public void returnBook() {
        this.returned = true;
    }

    // Check if the book is overdue
    public boolean isOverdue() {
        return !returned && LocalDate.now().isAfter(dueDate);
    }

    // Get a string representation of the borrowing record
    @Override
    public String toString() {
        return book.getDescription() + " (Due: " + dueDate + ")";
    }
} 