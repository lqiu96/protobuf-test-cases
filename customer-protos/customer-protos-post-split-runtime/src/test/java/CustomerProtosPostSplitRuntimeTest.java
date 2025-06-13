import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.example.protobuf.Book;
import com.example.protobuf.GetBookRequest;
import com.google.cloud.kms.v1.AsymmetricSignRequest;
import com.google.cloud.kms.v1.Digest;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.KeyRing;
import com.google.cloud.kms.v1.ListKeyRingsRequest;
import com.google.cloud.kms.v1.LocationName;
import com.google.cloud.kms.v1.MacSignResponse;
import com.google.cloud.kms.v1.ProtectionLevel;
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
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Duration;
import com.google.protobuf.FieldMask;
import com.google.protobuf.InvalidProtocolBufferException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

public class CustomerProtosPostSplitRuntimeTest extends BaseAdvancedUseCaseTestCases
    implements BaseJavaSdkTestCases {

  @Test
  void java_sdk_message() {
    KeyRing keyRing = KeyRing.newBuilder().setName("KeyRingName").build();
    assertEquals("KeyRingName", keyRing.getName());
    assertInstanceOf(com.shaded.google.protobuf.proto.GeneratedMessageV3.class, keyRing);
    assertInstanceOf(com.shaded.google.protobuf.proto.AbstractMessage.class, keyRing);
    assertInstanceOf(com.google.protobuf.Message.class, keyRing);
  }

  @Test
  void java_sdk_request() {
    AsymmetricSignRequest request =
        AsymmetricSignRequest.newBuilder()
            .setName("requestName")
            .setData(ByteString.copyFrom("Data", StandardCharsets.UTF_8))
            .setDigest(
                Digest.newBuilder()
                    .setSha256(ByteString.copyFrom("SHA", StandardCharsets.UTF_8))
                    .build())
            .build();
    assertEquals("requestName", request.getName());
    assertEquals("Data", request.getData().toString(StandardCharsets.UTF_8));
    assertEquals("SHA", request.getDigest().getSha256().toString(StandardCharsets.UTF_8));
    assertInstanceOf(com.shaded.google.protobuf.proto.GeneratedMessageV3.class, request);
    assertInstanceOf(com.shaded.google.protobuf.proto.AbstractMessage.class, request);
    assertInstanceOf(com.google.protobuf.Message.class, request);
  }

  @Test
  void customer_proto_request() {
    GetBookRequest request = GetBookRequest.newBuilder().setIsbn(1234).build();
    assertEquals(1234, request.getIsbn());
    assertInstanceOf(com.google.protobuf.GeneratedMessageV3.class, request);
    assertInstanceOf(com.google.protobuf.AbstractMessage.class, request);
    assertInstanceOf(com.google.protobuf.Message.class, request);
  }

  @Test
  void java_sdk_response() {
    MacSignResponse response =
        MacSignResponse.newBuilder()
            .setName("responseName")
            .setProtectionLevel(ProtectionLevel.EXTERNAL)
            .build();
    assertEquals("responseName", response.getName());
    assertEquals(ProtectionLevel.EXTERNAL, response.getProtectionLevel());
    assertInstanceOf(com.shaded.google.protobuf.proto.GeneratedMessageV3.class, response);
    assertInstanceOf(com.shaded.google.protobuf.proto.AbstractMessage.class, response);
    assertInstanceOf(com.google.protobuf.Message.class, response);
  }

  @Override
  @Test
  public void kms_list() {
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

  @Override
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

  @Override
  @Test
  void mergeFrom() throws IOException {
    Book fileBook =
        Book.newBuilder()
            .setIsbn(PARTIAL_ISBN)
            .setTitle(PARTIAL_TITLE)
            .setAuthorBytes(ByteString.copyFrom(PARTIAL_AUTHOR, StandardCharsets.UTF_8))
            .build();

    try (FileOutputStream outputStream = new FileOutputStream(partialPath.toFile())) {
      fileBook.writeTo(outputStream);
    }

    Book.Builder bookBuilder = Book.newBuilder();
    bookBuilder.mergeFrom(new FileInputStream(partialPath.toFile()));

    Book book = bookBuilder.build();
    assertEquals(PARTIAL_ISBN, book.getIsbn());
    assertEquals(PARTIAL_TITLE, book.getTitle());
    assertEquals(PARTIAL_AUTHOR, book.getAuthor());
  }

  @Override
  @Test
  void writeToFile_readFromFile() throws IOException {
    Book book =
        Book.newBuilder()
            .setIsbn(1234)
            .setTitle("myTitle")
            .setAuthorBytes(ByteString.copyFrom("myAuthor", StandardCharsets.UTF_8))
            .build();

    try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
      book.writeTo(outputStream);
    }

    Book newBook;
    try (FileInputStream inputStream = new FileInputStream(tempFile)) {
      newBook = Book.parseFrom(inputStream);
    }
    assertEquals(newBook.getIsbn(), book.getIsbn());
    assertEquals(newBook.getTitle(), book.getTitle());
    assertEquals(newBook.getAuthor(), book.getAuthor());
  }

  @Test
  void parser_fromByteString() throws InvalidProtocolBufferException {
    Book book = Book.newBuilder().setIsbn(1234).setAuthor("myAuthor").setTitle("myTitle").build();

    Book result = Book.parser().parseFrom(book.toByteString());
    assertEquals(result.getIsbn(), book.getIsbn());
    assertEquals(result.getAuthor(), book.getAuthor());
    assertEquals(result.getTitle(), book.getTitle());
  }

  @Override
  @Test
  void parser_fromByteArray() throws InvalidProtocolBufferException {
    Book book =
        Book.newBuilder()
            .setIsbn(1234)
            .setTitle("myTitle")
            .setAuthorBytes(ByteString.copyFrom("myAuthor", StandardCharsets.UTF_8))
            .build();

    Book result = Book.parser().parseFrom(book.toByteArray());
    assertEquals(result.getIsbn(), book.getIsbn());
    assertEquals(result.getTitle(), book.getTitle());
    assertEquals(result.getAuthor(), book.getAuthor());
  }

  @Override
  @Test
  void message_clear() {
    Book book =
        Book.newBuilder()
            .setIsbn(1234)
            .setTitle("myTitle")
            .setAuthorBytes(ByteString.copyFrom("myAuthor", StandardCharsets.UTF_8))
            .build();
    Book resetBook = book.toBuilder().clear().build();
    assertEquals(0, resetBook.getIsbn());
    assertEquals("", resetBook.getTitle());
  }

  @Override
  @Test
  void fieldDescriptors_get_set() {
    Book.Builder builder = Book.newBuilder();
    Descriptors.Descriptor descriptor = builder.getDescriptorForType();

    Descriptors.FieldDescriptor isbn = descriptor.findFieldByName("isbn");
    builder.setField(isbn, 1234);

    Descriptors.FieldDescriptor title = descriptor.findFieldByName("title");
    builder.setField(title, "myTitle");

    Descriptors.FieldDescriptor author = descriptor.findFieldByName("author");
    builder.setField(author, ByteString.copyFrom("myAuthor", StandardCharsets.UTF_8));

    Book book = builder.build();
    assertEquals(1234, book.getIsbn());
    assertEquals("myTitle", book.getTitle());
    assertEquals("myAuthor", book.getAuthor());
  }
}
