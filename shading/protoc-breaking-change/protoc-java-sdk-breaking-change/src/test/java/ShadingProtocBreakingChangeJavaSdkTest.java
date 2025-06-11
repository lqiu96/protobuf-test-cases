import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.example.protobuf.Book;
import com.google.cloud.kms.v1.KeyRing;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Timestamp;
import com.shaded.google.protobuf.proto.GeneratedMessageV4;
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

    // This class is shaded with the internal GeneratedMessageV4, even though customer's
    // protobuf-sdk has GeneratedMessageV3
    assertInstanceOf(GeneratedMessageV4.class, keyRing);
  }

  @Test
  void customer_notImpacted() {
    Book book = Book.newBuilder().setIsbn(1234).setAuthor("myAuthor").setTitle("myTitle").build();
    assertEquals(1234, book.getIsbn());
    assertEquals("myAuthor", book.getAuthor());
    assertEquals("myTitle", book.getTitle());

    assertInstanceOf(GeneratedMessageV3.class, book);
  }
}
