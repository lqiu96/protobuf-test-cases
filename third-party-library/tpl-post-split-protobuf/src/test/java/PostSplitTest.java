import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.protobuf.Any;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class PostSplitTest {

  @Test
  void any() {
    Any any = PostSplit.any();
    assertEquals("", any.getValue().toString(StandardCharsets.UTF_8));
  }

  @Test
  void kms_list() {
    PostSplit.kms_list();
  }
}
