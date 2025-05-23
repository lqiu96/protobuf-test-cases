import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.protobuf.Book;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.KeyRing;
import com.google.cloud.kms.v1.ListKeyRingsRequest;
import com.google.cloud.kms.v1.LocationName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

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

  @Test
  void kms_list() {
    try (KeyManagementServiceClient keyManagementServiceClient =
                 KeyManagementServiceClient.create()) {
      KeyManagementServiceClient.ListKeyRingsPagedResponse listKeyRingsPagedResponse =
              keyManagementServiceClient.listKeyRings(
                      ListKeyRingsRequest.newBuilder()
                              .setParent(LocationName.of("lawrence-test-project-2", "us-central1").toString())
                              .build());
      for (KeyRing keyRing : listKeyRingsPagedResponse.iterateAll()) {
        System.out.println(keyRing);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
