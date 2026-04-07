package librarymain;

// This interface defines what every book in the library must be able to do
public interface Book {
    // Gets the title of the book
    String getDescription(); 
    
    // Gets who wrote the book
    String getAuthor(); 
    
    // Checks if two books are exactly the same (same title and author)
    @Override
    boolean equals(Object obj);
    
    // Creates a unique number code for each book (helps with organizing books)
    @Override
    int hashCode();
}
