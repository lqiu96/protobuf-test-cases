import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.KeyRing;
import com.google.cloud.kms.v1.ListKeyRingsRequest;
import com.google.cloud.kms.v1.LocationName;
import com.google.protobuf.Any;
import com.google.protobuf.Message;
import java.io.IOException;
import java.util.List;

public class PostSplit {

  // Any exists in Protobuf-Api which is brought in as part of the Java SDK
  public static Any any() {
    return Any.newBuilder().build();
  }

  public static List<Message> messages() {
    return List.of(KeyRing.newBuilder().build(), ListKeyRingsRequest.newBuilder().build());
  }

  public static void kmsList() {
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
}
