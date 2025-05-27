import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
  void messages_instanceOf_protobufApi_throwsVerifyError() {
    assertThrows(VerifyError.class, PreSplit::messages);
  }

  @Timeout(value = 5)
  @Test
  void speech_recognize_throwsVerifyError() {
    assertThrows(VerifyError.class, PreSplit::speechRecognize);
  }

  @Timeout(value = 5)
  @Test
  void secret_manager_CRUD_throwsVerifyError() {
    assertThrows(VerifyError.class, PreSplit::secretManagerCRUD);
  }
}
