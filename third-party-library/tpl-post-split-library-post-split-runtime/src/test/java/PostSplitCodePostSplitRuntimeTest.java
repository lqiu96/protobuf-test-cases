import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.google.protobuf.Any;
import com.google.protobuf.Message;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class PostSplitCodePostSplitRuntimeTest {

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

  @Test
  void kms_list() {
    PostSplit.kmsList();
  }

  @Test
  void speech_recognize() {
    PostSplit.speechRecognize();
  }

  @Test
  void secret_manager_CRUD() {
    PostSplit.secretManagerCRUD();
  }

  @Test
  void notebook_operations() {
    PostSplit.notebooksOperations();
  }
}
