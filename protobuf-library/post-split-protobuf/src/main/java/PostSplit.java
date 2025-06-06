import com.google.cloud.kms.v1.Certificate;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.KeyRing;
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
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.Duration;
import com.google.protobuf.FieldMask;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

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
                  .setParent(
                      LocationName.of(System.getenv("PROJECT_ID"), System.getenv("LOCATION"))
                          .toString())
                  .build());
      for (KeyRing keyRing : listKeyRingsPagedResponse.iterateAll()) {
        System.out.println(keyRing);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // Speech has custom RPCs (recognize)
  public static void speechRecognize() {
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
        System.out.printf("Transcription: %s%n", alternative.getTranscript());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // Use SecretManager API to run through the basic CRUD operations
  public static void secretManagerCRUD() {
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

  public static void notebooksOperations() {
    try (NotebookServiceClient notebookServiceClient = NotebookServiceClient.create()) {
      Instance instance =
          notebookServiceClient
              .createInstanceAsync(
                  com.google.cloud.notebooks.v2.LocationName.of(
                          System.getenv("PROJECT_ID"), System.getenv("ZONE"))
                      .toString(),
                  Instance.newBuilder().build(),
                  "instance12345")
              .get();
      notebookServiceClient.deleteInstanceAsync(instance.getName()).get();
    } catch (IOException | ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static Certificate mergeFrom(Path path) throws IOException {
    Certificate.Builder certificateBuilder = Certificate.newBuilder();
    certificateBuilder.mergeFrom(new FileInputStream(path.toFile()));

    return certificateBuilder.build();
  }

  public static Certificate writeToFileReadFromFile(Certificate certificate) throws IOException {
    File certificateFile = File.createTempFile("certificate", null);

    try (FileOutputStream outputStream = new FileOutputStream(certificateFile)) {
      certificate.writeTo(outputStream);
    }

    Certificate newCertificate;
    try (FileInputStream inputStream = new FileInputStream(certificateFile)) {
      newCertificate = Certificate.parseFrom(inputStream);
    }

    certificateFile.delete();
    return newCertificate;
  }

  public static Certificate parserFromByteArray(Certificate certificate) throws InvalidProtocolBufferException {
    return Certificate.parser().parseFrom(certificate.toByteArray());
  }

  public static Certificate messageClear(Certificate certificate) {
    return certificate.toBuilder().clear().build();
  }
}
