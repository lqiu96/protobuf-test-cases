import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.protobuf.Any;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class TPLOlderThanJavaSDKTest {

  @Test
  void any() {
    Any any = PostSplitOlder.any();
    assertEquals("DEFAULT", any.getValue().toString(StandardCharsets.UTF_8));
  }
}
