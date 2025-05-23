import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.cloud.kms.v1.KeyRing;
import com.google.protobuf.TextFormat;
import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Test;

class NonGenCodeMethodsTest {

  @Test
  void testTextFormat() {
    int nanos = 1234;
    KeyRing.Builder builder =
        KeyRing.newBuilder().setCreateTime(Timestamp.newBuilder().setNanos(nanos).build());
    String result = TextFormat.printer().printToString(builder);
    assertTrue(result.contains(String.valueOf(nanos)));
  }
}
