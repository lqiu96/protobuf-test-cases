import com.example.protobuf.Book;

public class PostSplitGenCode {
  public static Book createBook(String author, int isbn, String title) {
    return Book.newBuilder().setAuthor(author).setIsbn(isbn).setTitle(title).build();
  }
}
