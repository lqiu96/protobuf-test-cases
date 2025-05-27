import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.google.protobuf.Message;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

// These tests cases test that existing client libraries in the Java SDK
// compiled with Protobuf-Java are able to run with the new post-split runtimes
class PreSplitTest {

  @Test
  void textFormat_protobufSdk() {
    assertEquals("value: \"Value\"\n", PreSplit.textFormat());
  }

  @Test
  void messages_instanceOf_protobufApi() {
    List<Message> messages = PreSplit.messages();
    for (Message message : messages) {
      assertInstanceOf(com.google.protobuf.GeneratedMessageV3.class, message);
      assertInstanceOf(com.google.protobuf.AbstractMessage.class, message);
      assertInstanceOf(com.google.protobuf.Message.class, message);
    }
  }

  @Timeout(value = 5)
  @Test
  void speech_recognize() {
    PreSplit.speechRecognize();
  }

  @Timeout(value = 5)
  @Test
  void secret_manager_CRUD() {
    PreSplit.secretManagerCRUD();
  }
}
