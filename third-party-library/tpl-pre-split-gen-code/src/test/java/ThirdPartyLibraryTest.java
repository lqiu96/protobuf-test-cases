import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.protobuf.Book;
import org.junit.jupiter.api.Test;

class ThirdPartyLibraryTest {

  @Test
  void preSplitGenCode() {
    String author = "myBookAuthor";
    int isbn = 1234;
    String title = "myBookTitle";
    Book book = PreSplitGenCode.book(author, isbn, title);
    assertEquals(title, book.getTitle());
    assertEquals(isbn, book.getIsbn());
    assertEquals(author, book.getAuthor());
  }

  @Test
  void speech_recognize() {
    PreSplitGenCode.speech();
  }

  @Test
  void secretmanager_create_delete() {
    PreSplitGenCode.secretmanager();
  }
}
