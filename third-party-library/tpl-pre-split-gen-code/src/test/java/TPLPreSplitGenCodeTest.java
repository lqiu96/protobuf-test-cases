import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.protobuf.Book;
import org.junit.jupiter.api.Test;

/**
 * This tests a third party library that has Protobuf-Java gen code that is compiled with
 * Protobuf-Java, but run with Split-Protobuf
 */
class TPLPreSplitGenCodeTest {

  @Test
  void createBook() {
    String author = "myBookAuthor";
    int isbn = 1234;
    String title = "myBookTitle";
    Book book = PreSplitGenCode.createBook(author, isbn, title);
    assertEquals(title, book.getTitle());
    assertEquals(isbn, book.getIsbn());
    assertEquals(author, book.getAuthor());
  }
}
