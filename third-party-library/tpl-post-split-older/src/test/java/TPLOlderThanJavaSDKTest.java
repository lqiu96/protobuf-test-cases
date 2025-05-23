import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.protobuf.Any;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class TPLOlderThanJavaSDKTest {

  @Test
  void any() {
    Any any = PostSplitOlder.any();
    assertEquals("", any.getValue().toString(StandardCharsets.UTF_8));
  }

  @Test
  void speech_recognize() {
    PostSplitOlder.speech();
  }

  @Test
  void secretmanager_create_delete() {
    PostSplitOlder.secretmanager();
  }
}
