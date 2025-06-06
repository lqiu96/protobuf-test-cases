import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

// These tests cases test that existing client libraries in the Java SDK
// compiled with Protobuf-Java are able to run with the new post-split runtimes
class PreSplitTest {

  @Test
  void textFormat_protobufSdk() {
    assertEquals("value: \"Value\"\n", PreSplit.textFormat());
  }

  // This expected to throw a Verify Error (binary breaking change) because the hierarchy
  // of messages was updated and is no longer the same:
  // Pre-Split: GeneratedMessageV3 -> AbstractMessage -> Message
  // Post-Split: GeneratedMessageV3 -> SingleFieldBuilder -> Message
  // This requires the messages to be rebuilt
  @Test
  void messages_instanceOf_protobufApi_throwsVerifyError() {
    assertThrows(VerifyError.class, PreSplit::messages);
  }

  // This expected to throw a Verify Error (binary breaking change) because the hierarchy
  // of messages was updated and is no longer the same:
  // Pre-Split: GeneratedMessageV3 -> AbstractMessage -> Message
  // Post-Split: GeneratedMessageV3 -> SingleFieldBuilder -> Message
  // This requires the messages to be rebuilt
  @Test
  void speech_recognize_throwsVerifyError() {
    assertThrows(VerifyError.class, PreSplit::speechRecognize);
  }

  // This expected to throw a Verify Error (binary breaking change) because the hierarchy
  // of messages was updated and is no longer the same:
  // Pre-Split: GeneratedMessageV3 -> AbstractMessage -> Message
  // Post-Split: GeneratedMessageV3 -> SingleFieldBuilder -> Message
  // This requires the messages to be rebuilt
  @Test
  void secret_manager_CRUD_throwsVerifyError() {
    assertThrows(VerifyError.class, PreSplit::secretManagerCRUD);
  }
}
