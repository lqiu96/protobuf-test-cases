import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.example.protobuf.Book;
import com.google.cloud.kms.v1.KeyRing;
import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Test;

class ShadingProtocBreakingChangeJavaSdkTest {

  @Test
  void javasdk_notImpacted() {
    KeyRing keyRing =
        KeyRing.newBuilder()
            .setName("KeyRingName")
            .setCreateTime(Timestamp.newBuilder().setSeconds(1234).build())
            .build();
    assertEquals("KeyRingName", keyRing.getName());
    assertEquals(1234, keyRing.getCreateTime().getSeconds());

    // This Java SDK proto message is shaded with its own version of GeneratedMessageV4 from
    // protobuf-sdk v2. It's an incompatible version with the version that the customer is using.
    assertInstanceOf(com.shaded.google.protobuf.proto.GeneratedMessageV4.class, keyRing);
  }

  @Test
  void customer_notImpacted() {
    Book book = Book.newBuilder().setIsbn(1234).setAuthor("myAuthor").setTitle("myTitle").build();
    assertEquals(1234, book.getIsbn());
    assertEquals("myAuthor", book.getAuthor());
    assertEquals("myTitle", book.getTitle());

    // This version is from the customer defined version (protobuf-sdk v1)
    assertInstanceOf(com.google.protobuf.GeneratedMessageV3.class, book);
  }
}
