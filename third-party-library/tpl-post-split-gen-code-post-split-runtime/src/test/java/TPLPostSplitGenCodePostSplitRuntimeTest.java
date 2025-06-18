import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.protobuf.Book;
import org.junit.jupiter.api.Test;

class TPLPostSplitGenCodePostSplitRuntimeTest {

  @Test
  void createBook() {
    String author = "myBookAuthor";
    int isbn = 1234;
    String title = "myBookTitle";
    Book book = PostSplitGenCode.createBook(author, isbn, title);
    assertEquals(title, book.getTitle());
    assertEquals(isbn, book.getIsbn());
    assertEquals(author, book.getAuthor());
  }
}
