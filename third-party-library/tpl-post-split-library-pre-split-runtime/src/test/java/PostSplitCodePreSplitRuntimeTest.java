import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.google.protobuf.Any;
import com.google.protobuf.Message;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

// These tests cases test that shaded client libraries in the Java SDK
// compiled with Split-Protobuf are able to run with post-split runtimes
class PostSplitCodePreSplitRuntimeTest {

  @Test
  void any() {
    Any any = PostSplit.any();
    assertEquals("", any.getValue().toString(StandardCharsets.UTF_8));
  }

  @Test
  void message_instanceOf_shadedProtobuf() {
    List<Message> messages = PostSplit.messages();
    for (Message message : messages) {
      assertInstanceOf(com.shaded.google.protobuf.proto.GeneratedMessageV3.class, message);
      assertInstanceOf(com.shaded.google.protobuf.proto.AbstractMessage.class, message);
      assertInstanceOf(Message.class, message);
    }
  }

  @Timeout(value = 5, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
  @Test
  void kms_list() {
    PostSplit.kmsList();
  }

  @Timeout(value = 5, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
  @Test
  void speech_recognize() {
    PostSplit.speechRecognize();
  }

  @Timeout(value = 5, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
  @Test
  void secret_manager_CRUD() {
    PostSplit.secretManagerCRUD();
  }
}
