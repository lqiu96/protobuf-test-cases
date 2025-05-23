import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.protobuf.Any;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class TPLNewerThanJavaSDKTest {

  @Test
  void any() {
    Any any = PostSplitNewer.any();
    assertEquals("", any.getValue().toString(StandardCharsets.UTF_8));
  }
}
