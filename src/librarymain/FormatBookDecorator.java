package librarymain;

public class FormatBookDecorator implements Book {
    private Book book;

    public FormatBookDecorator(Book book) {
        this.book = book;
        
    }

    @Override
    public String getDescription() {
        return book.getDescription();
    }

    @Override
    public String getAuthor() {
        return book.getAuthor();
    }
}
