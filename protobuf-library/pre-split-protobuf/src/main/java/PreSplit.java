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
import com.google.protobuf.Parser;
import com.google.protobuf.TextFormat;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class PreSplit {

  // TextFormat is now in Protobuf-Sdk in the post-split repo
  public static String textFormat() {
    return TextFormat.printer()
        .printToString(
            Any.newBuilder().setValue(ByteString.copyFrom("Value", StandardCharsets.UTF_8)));
  }

  public static List<Message> messages() {
    return List.of(
        SpeechRecognitionResult.newBuilder().build(),
        Secret.newBuilder().build(),
        RecognitionConfig.newBuilder().build(),
        Replication.newBuilder().build());
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

  public static Message mergeFrom(Message.Builder builder, Path path) throws IOException {
    builder.mergeFrom(new FileInputStream(path.toFile()));

    return builder.build();
  }

  public static void writeToFile(Message message, File file) throws IOException {
    try (FileOutputStream outputStream = new FileOutputStream(file)) {
      message.writeTo(outputStream);
    }
  }

  public static Message readFromFile(File file, Parser<? extends Message> parser)
      throws IOException {
    try (FileInputStream inputStream = new FileInputStream(file)) {
      return parser.parseFrom(inputStream);
    }
  }

  public static Message writeToFileReadFromFile(Message message, Parser<? extends Message> parser)
      throws IOException {
    File messageFile = File.createTempFile("message", null);

    writeToFile(message, messageFile);
    Message newMessage = readFromFile(messageFile, parser);

    messageFile.delete();
    return newMessage;
  }

  public static Message parserFromByteArray(Message message) throws InvalidProtocolBufferException {
    return message.getParserForType().parseFrom(message.toByteArray());
  }

  public static Message messageClear(Message message) {
    return message.toBuilder().clear().build();
  }
}
