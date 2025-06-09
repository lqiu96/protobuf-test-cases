import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.google.cloud.kms.v1.KeyRing;
import com.google.protobuf.Timestamp;
import com.shaded.google.protobuf.proto.GeneratedMessageV3;
import org.junit.jupiter.api.Test;

class ProtocCustomerBreakingChange {

  @Test
  void javasdk_notImpacted() {
    KeyRing keyRing =
        KeyRing.newBuilder()
            .setName("KeyRingName")
            .setCreateTime(Timestamp.newBuilder().setSeconds(1234).build())
            .build();
    assertEquals("KeyRingName", keyRing.getName());
    assertEquals(1234, keyRing.getCreateTime().getSeconds());

    // Even though protobuf-sdk brings in GeneratedMessageV4, this class is still under
    // GeneratedMessageV3
    assertInstanceOf(GeneratedMessageV3.class, keyRing);
  }

  // Do not test customer impact as the code can't compile (GeneratedMessageV3 doesn't exist)
}
