import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.protobuf.Book;
import com.google.cloud.kms.v1.KeyRing;
import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Test;

class CustomerBreakingChange {

  @Test
  void javasdk_notImpacted() {
    KeyRing keyRing =
        KeyRing.newBuilder()
            .setName("KeyRingName")
            .setCreateTime(Timestamp.newBuilder().setSeconds(1234).build())
            .build();
    assertEquals("KeyRingName", keyRing.getName());
    assertEquals(1234, keyRing.getCreateTime().getSeconds());
  }

  // Customer is bringing in a major version of the unstable library
  // Their code may experience breaking changes
  @Test
  void customer_breakingChange() {
    assertThrows(ExceptionInInitializerError.class, Book::getDefaultInstance);
  }
}
