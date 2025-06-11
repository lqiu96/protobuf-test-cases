import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.google.cloud.kms.v1.KeyRing;
import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Test;

class ShadingProtocBreakingChangeCustomerTest {

  @Test
  void javasdk_notImpacted() {
    KeyRing keyRing =
        KeyRing.newBuilder()
            .setName("KeyRingName")
            .setCreateTime(Timestamp.newBuilder().setSeconds(1234).build())
            .build();
    assertEquals("KeyRingName", keyRing.getName());
    assertEquals(1234, keyRing.getCreateTime().getSeconds());

    // Even though the customer brings in a version of protobuf-sdk v2 with in GeneratedMessageV4
    // (major version bump that isn't compatible with protobuf-sdk v1 in java-sdk), the Java SDK
    // is shaded so it uses its own bundled version of GeneratedMessageV3
    assertInstanceOf(com.shaded.google.protobuf.proto.GeneratedMessageV3.class, keyRing);
  }

  // Do not test customer impact as the code can't compile (GeneratedMessageV3 doesn't exist)
}
