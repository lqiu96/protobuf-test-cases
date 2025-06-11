import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.protobuf.Book;
import com.google.cloud.kms.v1.ListKeyRingsRequest;
import org.junit.jupiter.api.Test;

class ShadingRuntimeBreakingChangeJavaSdkTest {

  @Test
  void javasdk_impacted() {
    // The Java SDK is using a version of protobuf-sdk that throws a RuntimeException. This version
    // is bundled as part of the Java SDK and is not the version the customer specified.
    assertThrows(ExceptionInInitializerError.class, ListKeyRingsRequest::getDefaultInstance);
  }

  @Test
  void customer_notImpacted() {
    Book book = Book.newBuilder().setIsbn(1234).setAuthor("myAuthor").setTitle("myTitle").build();
    assertEquals(1234, book.getIsbn());
    assertEquals("myAuthor", book.getAuthor());
    assertEquals("myTitle", book.getTitle());
  }
}
