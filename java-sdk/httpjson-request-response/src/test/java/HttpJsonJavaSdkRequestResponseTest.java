import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.ListKeyRingsRequest;
import com.google.cloud.kms.v1.LocationName;
import com.google.cloud.notebooks.v2.Instance;
import com.google.cloud.notebooks.v2.NotebookServiceClient;
import com.google.cloud.secretmanager.v1.ProjectName;
import com.google.cloud.secretmanager.v1.Replication;
import com.google.cloud.secretmanager.v1.Secret;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.UpdateSecretRequest;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.Duration;
import com.google.protobuf.FieldMask;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

class HttpJsonJavaSdkRequestResponseTest implements BaseJavaSdkTestCases {

  @Override
  @Test
  public void kms_list() {
    try (KeyManagementServiceClient keyManagementServiceClient =
        KeyManagementServiceClient.create()) {
      keyManagementServiceClient.listKeyRings(
          ListKeyRingsRequest.newBuilder()
              .setParent(
                  LocationName.of(System.getenv("PROJECT_ID"), System.getenv("LOCATION"))
                      .toString())
              .build());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // Speech has custom RPCs (recognize)
  @Override
  @Test
  public void speech_recognize() {
    try (SpeechClient speechClient = SpeechClient.create()) {
      String gcsUri = "gs://cloud-samples-data/speech/brooklyn_bridge.raw";
      RecognitionConfig config =
          RecognitionConfig.newBuilder()
              .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
              .setSampleRateHertz(16000)
              .setLanguageCode("en-US")
              .build();
      RecognitionAudio audio = RecognitionAudio.newBuilder().setUri(gcsUri).build();
      RecognizeResponse response = speechClient.recognize(config, audio);
      List<SpeechRecognitionResult> results = response.getResultsList();

      for (SpeechRecognitionResult result : results) {
        SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
        assertEquals("how old is the Brooklyn Bridge", alternative.getTranscript());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // Use SecretManager API to run through the basic CRUD operations
  @Override
  @Test
  public void secret_manager_CRUD() {
    String secretId = String.format("secret%s", UUID.randomUUID().toString().substring(0, 6));
    try (SecretManagerServiceClient secretManagerServiceClient =
        SecretManagerServiceClient.create()) {
      ProjectName projectName = ProjectName.of(System.getenv("PROJECT_ID"));

      Duration ttl = Duration.newBuilder().setSeconds(900).build();

      Secret secret =
          secretManagerServiceClient.createSecret(
              projectName,
              secretId,
              Secret.newBuilder()
                  .setReplication(
                      Replication.newBuilder()
                          .setAutomatic(Replication.Automatic.newBuilder().build())
                          .build())
                  .setTtl(ttl)
                  .build());
      secretManagerServiceClient.updateSecret(
          UpdateSecretRequest.newBuilder()
              .setUpdateMask(FieldMask.newBuilder().addPaths("ttl").build())
              .setSecret(secret.toBuilder().setTtl(Duration.newBuilder().setSeconds(1000)))
              .build());

      SecretManagerServiceClient.ListSecretsPagedResponse listSecretsPagedResponse =
          secretManagerServiceClient.listSecrets(projectName);
      for (Secret s : listSecretsPagedResponse.iterateAll()) {
        secretManagerServiceClient.deleteSecret(s.getName());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // Use Java-Notebooks to test LROs
  @Override
  @Test
  public void notebook_operations() {
    String id = UUID.randomUUID().toString().substring(0, 6);
    try (NotebookServiceClient notebookServiceClient = NotebookServiceClient.create()) {
      Instance instance =
          notebookServiceClient
              .createInstanceAsync(
                  com.google.cloud.notebooks.v2.LocationName.of(
                          System.getenv("PROJECT_ID"), System.getenv("ZONE"))
                      .toString(),
                  Instance.newBuilder().build(),
                  String.format("instance%s", id))
              .get();
      notebookServiceClient.deleteInstanceAsync(instance.getName()).get();
    } catch (IOException | ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
