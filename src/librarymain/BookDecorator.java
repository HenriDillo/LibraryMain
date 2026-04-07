package librarymain;

// This class adds extra features to a book
public abstract class BookDecorator implements Book {
    protected Book decoratedBook;

    public BookDecorator(Book decoratedBook) {
        this.decoratedBook = decoratedBook;
    }

    // Get the book's description
    @Override
    public String getDescription() {
        return decoratedBook.getDescription();
    }

    // Check if two books are the same
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BookDecorator that = (BookDecorator) obj;
        return decoratedBook.equals(that.decoratedBook);
    }

    // Get a unique number for the book
    @Override
    public int hashCode() {
        return decoratedBook.hashCode();
    }
}

// This class adds a bestseller label to a book
class BestsellerDecorator extends BookDecorator {
    public BestsellerDecorator(Book book) {
        super(book);
    }

    // Get the book's description with a bestseller label
    @Override
    public String getDescription() {
        return decoratedBook.getDescription();
    }

    // Get the book's author
    @Override
    public String getAuthor() {
        return decoratedBook.getAuthor();
    }
}

// This class adds an e-book label to a book
class EBookDecorator extends BookDecorator {
    public EBookDecorator(Book book) {
        super(book);
    }

    // Get the book's description with an e-book label
    @Override
    public String getDescription() {
        return decoratedBook.getDescription();
    }

    // Get the book's author
    @Override
    public String getAuthor() {
        return decoratedBook.getAuthor();
    }
} 