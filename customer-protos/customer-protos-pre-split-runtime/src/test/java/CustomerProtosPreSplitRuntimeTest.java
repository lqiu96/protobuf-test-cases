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
import com.google.cloud.secretmanager.v1.ProjectName;
import com.google.cloud.secretmanager.v1.Replication;
import com.google.cloud.secretmanager.v1.Secret;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.UpdateSecretRequest;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.protobuf.ByteString;
import com.google.protobuf.Duration;
import com.google.protobuf.FieldMask;
import com.google.protobuf.InvalidProtocolBufferException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CustomerProtosPreSplitRuntimeTest {

  private static Path bookPartialPath;
  private static File tempBookFile;

  private static final int PARTIAL_ISBN = 9999;
  private static final String PARTIAL_TITLE = "myNewTitle";
  private static final String PARTIAL_AUTHOR = "myNewAuthor";

  @BeforeAll
  static void setup() throws IOException {
    tempBookFile = File.createTempFile("certificate", null);
    bookPartialPath = Paths.get("src", "test", "resources", "book_partial.txt");

    Book book =
        Book.newBuilder()
            .setIsbn(PARTIAL_ISBN)
            .setTitle(PARTIAL_TITLE)
            .setAuthorBytes(ByteString.copyFrom(PARTIAL_AUTHOR, StandardCharsets.UTF_8))
            .build();

    try (FileOutputStream outputStream = new FileOutputStream(bookPartialPath.toFile())) {
      book.writeTo(outputStream);
    }
  }

  @AfterAll
  static void cleanUp() {
    tempBookFile.delete();
  }

  @Test
  void java_sdk_message() {
    KeyRing keyRing = KeyRing.newBuilder().setName("KeyRingName").build();
    assertEquals("KeyRingName", keyRing.getName());
    assertInstanceOf(com.shaded.google.protobuf.proto.GeneratedMessageV3.class, keyRing);
    assertInstanceOf(com.shaded.google.protobuf.proto.AbstractMessage.class, keyRing);
    assertInstanceOf(com.google.protobuf.Message.class, keyRing);
  }

  @Test
  void customer_proto_message() {
    Book book = Book.newBuilder().setAuthor("BookAuthor").build();
    assertEquals("BookAuthor", book.getAuthor());
    assertInstanceOf(com.google.protobuf.GeneratedMessageV3.class, book);
    assertInstanceOf(com.google.protobuf.AbstractMessage.class, book);
    assertInstanceOf(com.google.protobuf.Message.class, book);
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

  @Test
  void kms_list() {
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
  @Test
  void speech_recognize() {
    try (SpeechClient speechClient = SpeechClient.create()) {
      String gcsUri = "gs://cloud-samples-data/speech/brooklyn_bridge.raw";
      RecognitionConfig config =
          RecognitionConfig.newBuilder()
              .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
              .setSampleRateHertz(16000)
              .setLanguageCode("en-US")
              .build();
      RecognitionAudio audio = RecognitionAudio.newBuilder().setUri(gcsUri).build();
      speechClient.recognize(config, audio);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // Use SecretManager API to run through the basic CRUD operations
  @Test
  void secret_manager_CRUD() {
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

  @Test
  void mergeFrom() throws IOException {
    Book.Builder bookBuilder = Book.newBuilder();
    bookBuilder.mergeFrom(new FileInputStream(bookPartialPath.toFile()));

    Book book = bookBuilder.build();
    assertEquals(PARTIAL_ISBN, book.getIsbn());
    assertEquals(PARTIAL_TITLE, book.getTitle());
    assertEquals(PARTIAL_AUTHOR, book.getAuthor());
  }

  @Test
  void writeToFile_readFromFile() throws IOException {
    Book book =
        Book.newBuilder()
            .setIsbn(1234)
            .setTitle("myTitle")
            .setAuthorBytes(ByteString.copyFrom("myAuthor", StandardCharsets.UTF_8))
            .build();

    try (FileOutputStream outputStream = new FileOutputStream(tempBookFile)) {
      book.writeTo(outputStream);
    }

    Book newBook;
    try (FileInputStream inputStream = new FileInputStream(tempBookFile)) {
      newBook = Book.parseFrom(inputStream);
    }
    assertEquals(newBook.getIsbn(), book.getIsbn());
    assertEquals(newBook.getTitle(), book.getTitle());
    assertEquals(newBook.getAuthor(), book.getAuthor());
  }

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
}
