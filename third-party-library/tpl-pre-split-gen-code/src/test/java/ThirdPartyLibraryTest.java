import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.protobuf.Book;
import org.junit.jupiter.api.Test;

// This tests a third-party library has older Protoc and compiled with newer Protobuf-Java
// version and run with later protobuf-sdk version
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
}
