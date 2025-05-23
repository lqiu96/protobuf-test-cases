import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.KeyRing;
import com.google.cloud.kms.v1.ListKeyRingsRequest;
import com.google.cloud.kms.v1.LocationName;
import com.google.protobuf.Any;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class PostSplitProtobufJavaSDKTest {

  @Test
  void any() {
    Any any = PostSplitNewer.any();
    assertEquals("", any.getValue().toString(StandardCharsets.UTF_8));
  }

  @Test
  void kms_list() {
    try (KeyManagementServiceClient keyManagementServiceClient =
        KeyManagementServiceClient.create()) {
      KeyManagementServiceClient.ListKeyRingsPagedResponse listKeyRingsPagedResponse =
          keyManagementServiceClient.listKeyRings(
              ListKeyRingsRequest.newBuilder()
                  .setParent(LocationName.of("lawrence-test-project-2", "us-central1").toString())
                  .build());
      for (KeyRing keyRing : listKeyRingsPagedResponse.iterateAll()) {
        System.out.println(keyRing);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void speech_recognize() {
    PostSplitNewer.speech();
  }

  @Test
  void secretmanager_create_delete() {
    PostSplitNewer.secretmanager();
  }
}
