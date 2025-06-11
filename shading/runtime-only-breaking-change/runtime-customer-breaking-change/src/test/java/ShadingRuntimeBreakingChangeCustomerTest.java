import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.protobuf.Book;
import com.google.cloud.kms.v1.KeyRing;
import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Test;

class ShadingRuntimeBreakingChangeCustomerTest {

  @Test
  void javasdk_notImpacted() {
    KeyRing keyRing =
        KeyRing.newBuilder()
            .setName("KeyRingName")
            .setCreateTime(Timestamp.newBuilder().setSeconds(1234).build())
            .build();
    assertEquals("KeyRingName", keyRing.getName());
    assertEquals(1234, keyRing.getCreateTime().getSeconds());

    // Bundled version of Protobuf is used and doesn't throw a RuntimeException when invoked
    assertInstanceOf(com.shaded.google.protobuf.proto.GeneratedMessageV3.class, keyRing);
  }

  @Test
  void customer_impacted() {
    // The customer is using a version of protobuf-sdk that throws a RuntimeException. This version
    // is different from the version bundled as part of the Java SDK.
    assertThrows(ExceptionInInitializerError.class, Book::getDefaultInstance);
  }
}
