package librarymain;

// This is a regular book with just a title and author (no special features)
public class BasicBook implements Book {
    // Store the book's title
    private String title;
    
    // Store the book's author
    private String author;

    // When creating a new book, we need its title and author
    public BasicBook(String title, String author) {
        this.title = title;
        this.author = author;
    }

    // Returns the book's title when asked
    @Override
    public String getDescription() {
        return title;
    }

    // Returns who wrote the book
    @Override
    public String getAuthor() {
        return author;
    }

    // Checks if this book is exactly the same as another book
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;  // Same memory location = same book
        if (obj == null || getClass() != obj.getClass()) return false;  // Not even a book
        BasicBook other = (BasicBook) obj;
        return title.equals(other.title) && author.equals(other.author);  // Check title and author match
    }

    // Creates a unique number for this book based on title and author
    @Override
    public int hashCode() {
        return title.hashCode() * 31 + author.hashCode();
    }
}
