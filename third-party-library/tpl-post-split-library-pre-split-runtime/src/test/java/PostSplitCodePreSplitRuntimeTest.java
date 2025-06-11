import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.google.cloud.kms.v1.Certificate;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * This tests that a third-party library that is compiled with Split-Protobuf is able to run with
 * Protobuf-Java. The third-party library has modules Post-Split Protobuf shaded in the Java SDK.
 */
class PostSplitCodePreSplitRuntimeTest {

  private static File tempCertificateFile;
  private static Path certificatePartialPath;

  private static final String PARTIAL_ISSUER = "randomIssuer";
  private static final boolean PARTIAL_PARSED = true;
  private static final String PARTIAL_SHA256 = "randomSHA256";
  private static final int PARTIAL_SECONDS = 1234;
  private static final int PARTIAL_NANOS = 5678;

  @BeforeAll
  static void setup() throws IOException {
    tempCertificateFile = File.createTempFile("certificate", null);
    certificatePartialPath = Paths.get("src", "test", "resources", "certificate_partial.txt");

    File certificatePartialFile = certificatePartialPath.toFile();
    // Create the directories if it doesn't already exist
    certificatePartialFile.getParentFile().mkdirs();

    Certificate certificate =
        Certificate.newBuilder()
            .setIssuer(PARTIAL_ISSUER)
            .setParsed(PARTIAL_PARSED)
            .setSha256FingerprintBytes(ByteString.copyFrom(PARTIAL_SHA256, StandardCharsets.UTF_8))
            .setNotAfterTime(
                Timestamp.newBuilder().setSeconds(PARTIAL_SECONDS).setNanos(PARTIAL_NANOS).build())
            .build();

    try (FileOutputStream outputStream = new FileOutputStream(certificatePartialFile)) {
      certificate.writeTo(outputStream);
    }
  }

  @Test
  void any() {
    Any any = PostSplit.any();
    assertEquals("", any.getValue().toString(StandardCharsets.UTF_8));
  }

  @Test
  void message_instanceOf_shadedProtobuf() {
    List<Message> messages = PostSplit.messages();
    for (Message message : messages) {
      assertInstanceOf(com.shaded.google.protobuf.proto.GeneratedMessageV3.class, message);
      assertInstanceOf(com.shaded.google.protobuf.proto.AbstractMessage.class, message);
      assertInstanceOf(Message.class, message);
    }
  }

  @Timeout(value = 5, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
  @Test
  void kms_list() {
    PostSplit.kmsList();
  }

  @Timeout(value = 5, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
  @Test
  void speech_recognize() {
    PostSplit.speechRecognize();
  }

  @Timeout(value = 5, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
  @Test
  void secret_manager_CRUD() {
    PostSplit.secretManagerCRUD();
  }

  @Test
  void mergeFrom() throws IOException {
    Certificate.Builder certificateBuilder = Certificate.newBuilder();
    certificateBuilder.mergeFrom(new FileInputStream(certificatePartialPath.toFile()));

    Certificate certificate = certificateBuilder.build();
    assertEquals(PARTIAL_ISSUER, certificate.getIssuer());
    assertEquals(PARTIAL_PARSED, certificate.getParsed());
    assertEquals(PARTIAL_SHA256, certificate.getSha256Fingerprint());
    assertEquals(PARTIAL_SECONDS, certificate.getNotAfterTime().getSeconds());
    assertEquals(PARTIAL_NANOS, certificate.getNotAfterTime().getNanos());
  }

  @Test
  void writeToFile_readFromFile() throws IOException {
    Certificate certificate =
        Certificate.newBuilder()
            .setIssuer("Issuer")
            .setParsed(false)
            .setSha256FingerprintBytes(ByteString.copyFrom("SHA256", StandardCharsets.UTF_8))
            .setNotAfterTime(Timestamp.newBuilder().setSeconds(50).setNanos(100).build())
            .build();

    try (FileOutputStream outputStream = new FileOutputStream(tempCertificateFile)) {
      certificate.writeTo(outputStream);
    }

    Certificate newCertificate;
    try (FileInputStream inputStream = new FileInputStream(tempCertificateFile)) {
      newCertificate = Certificate.parseFrom(inputStream);
    }
    assertEquals(certificate.getIssuer(), newCertificate.getIssuer());
    assertEquals(certificate.getParsed(), newCertificate.getParsed());
    assertEquals(
        certificate.getSha256FingerprintBytes(), newCertificate.getSha256FingerprintBytes());
    assertEquals(
        certificate.getNotAfterTime().getSeconds(), newCertificate.getNotAfterTime().getSeconds());
    assertEquals(
        certificate.getNotAfterTime().getNanos(), newCertificate.getNotAfterTime().getNanos());
  }

  @Test
  void parser_fromByteArray() throws InvalidProtocolBufferException {
    Certificate certificate =
        Certificate.newBuilder()
            .setIssuer("Issuer")
            .setParsed(false)
            .setSha256Fingerprint("SHA256")
            .setNotAfterTime(Timestamp.newBuilder().setSeconds(50).setNanos(100).build())
            .build();

    Certificate result = Certificate.parser().parseFrom(certificate.toByteArray());
    assertEquals(result.getIssuer(), certificate.getIssuer());
    assertEquals(result.getParsed(), certificate.getParsed());
    assertEquals(result.getSha256FingerprintBytes(), certificate.getSha256FingerprintBytes());
    assertEquals(result.getNotAfterTime().getSeconds(), certificate.getNotAfterTime().getSeconds());
    assertEquals(result.getNotAfterTime().getNanos(), certificate.getNotAfterTime().getNanos());
  }

  @Test
  void message_clear() {
    Certificate certificate =
        Certificate.newBuilder()
            .setIssuer("Issuer")
            .setParsed(false)
            .setSha256Fingerprint("SHA256")
            .setNotAfterTime(Timestamp.newBuilder().setSeconds(50).setNanos(100).build())
            .build();
    Certificate resetCertificate = certificate.toBuilder().clear().build();
    assertEquals("", resetCertificate.getIssuer());
    assertEquals(false, resetCertificate.getParsed());
    assertEquals("", resetCertificate.getSha256Fingerprint());
    assertEquals(0, resetCertificate.getNotAfterTime().getSeconds());
    assertEquals(0, resetCertificate.getNotAfterTime().getNanos());
  }
}
