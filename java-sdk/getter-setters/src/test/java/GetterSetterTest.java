import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.cloud.kms.v1.AsymmetricSignRequest;
import com.google.cloud.kms.v1.Digest;
import com.google.cloud.kms.v1.KeyRing;
import com.google.cloud.kms.v1.MacSignResponse;
import com.google.cloud.kms.v1.ProtectionLevel;
import com.google.protobuf.ByteString;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class GetterSetterTest {

  @Test
  void message() {
    KeyRing keyRing = KeyRing.newBuilder().setName("KeyRingName").build();
    assertEquals("KeyRingName", keyRing.getName());
  }

  @Test
  void request() {
    AsymmetricSignRequest request =
        AsymmetricSignRequest.newBuilder()
            .setName("requestName")
            .setData(ByteString.copyFrom("Data", StandardCharsets.UTF_8))
            .setDigest(
                Digest.newBuilder()
                    .setSha256(ByteString.copyFrom("SHA", StandardCharsets.UTF_8))
                    .build())
            .build();
    assertEquals("requestName", request.getName());
    assertEquals("Data", request.getData().toString(StandardCharsets.UTF_8));
    assertEquals("SHA", request.getDigest().getSha256().toString(StandardCharsets.UTF_8));
  }

  @Test
  void response() {
    MacSignResponse macSignResponse =
        MacSignResponse.newBuilder()
            .setName("responseName")
            .setProtectionLevel(ProtectionLevel.EXTERNAL)
            .build();
    assertEquals("responseName", macSignResponse.getName());
    assertEquals(ProtectionLevel.EXTERNAL, macSignResponse.getProtectionLevel());
  }
}
