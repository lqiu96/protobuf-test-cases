import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.cloud.kms.v1.Certificate;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Timestamp;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * This tests that a third-party library that is compiled with Protobuf-Java is able to run with
 * Split-Protobuf
 */
// These tests cases test that existing client libraries in the Java SDK
// compiled with Protobuf-Java are able to run with the new post-split runtimes
@Disabled
class TPLPreSplitCodePostSplitRuntimeTestHelper extends BaseTestHelper {

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

  // TODO: Currently disabled as PreSplit doesn't have an implementation
  @Override
  @Disabled
  @Test
  void kms_list() {}

  // This expected to throw a Verify Error (binary breaking change) because the hierarchy
  // of messages was updated and is no longer the same:
  // Pre-Split: GeneratedMessageV3 -> AbstractMessage -> Message
  // Post-Split: GeneratedMessageV3 -> SingleFieldBuilder -> Message
  // This requires the messages to be rebuilt
  @Override
  @Test
  void speech_recognize() {
    assertThrows(VerifyError.class, PreSplit::speechRecognize);
  }

  // This expected to throw a Verify Error (binary breaking change) because the hierarchy
  // of messages was updated and is no longer the same:
  // Pre-Split: GeneratedMessageV3 -> AbstractMessage -> Message
  // Post-Split: GeneratedMessageV3 -> SingleFieldBuilder -> Message
  // This requires the messages to be rebuilt
  @Override
  @Test
  void secret_manager_CRUD() {
    assertThrows(VerifyError.class, PreSplit::secretManagerCRUD);
  }

  // TODO: Currently disabled as PreSplit doesn't have an implementation
  @Override
  @Disabled
  @Test
  void notebook_operations() {}

  @Override
  @Test
  void mergeFrom() throws IOException {
    Certificate fileCertificate =
        Certificate.newBuilder()
            .setIssuer(PARTIAL_ISSUER)
            .setParsed(PARTIAL_PARSED)
            .setSha256FingerprintBytes(ByteString.copyFrom(PARTIAL_SHA256, StandardCharsets.UTF_8))
            .setNotAfterTime(
                Timestamp.newBuilder().setSeconds(PARTIAL_SECONDS).setNanos(PARTIAL_NANOS).build())
            .build();
    PreSplit.writeToFile(fileCertificate, certificatePartialPath.toFile());

    Certificate.Builder certificateBuilder = Certificate.newBuilder();
    certificateBuilder.mergeFrom(new FileInputStream(certificatePartialPath.toFile()));

    Certificate certificate = certificateBuilder.build();
    assertEquals(PARTIAL_ISSUER, certificate.getIssuer());
    assertEquals(PARTIAL_PARSED, certificate.getParsed());
    assertEquals(PARTIAL_SHA256, certificate.getSha256Fingerprint());
    assertEquals(PARTIAL_SECONDS, certificate.getNotAfterTime().getSeconds());
    assertEquals(PARTIAL_NANOS, certificate.getNotAfterTime().getNanos());
  }

  @Override
  @Test
  void writeToFile_readFromFile() throws IOException {
    Certificate certificate =
        Certificate.newBuilder()
            .setIssuer("Issuer")
            .setParsed(false)
            .setSha256FingerprintBytes(ByteString.copyFrom("SHA256", StandardCharsets.UTF_8))
            .setNotAfterTime(Timestamp.newBuilder().setSeconds(50).setNanos(100).build())
            .build();

    Certificate newCertificate =
        (Certificate) PreSplit.writeToFileReadFromFile(certificate, certificate.getParserForType());
    assertEquals(certificate.getIssuer(), newCertificate.getIssuer());
    assertEquals(certificate.getParsed(), newCertificate.getParsed());
    assertEquals(
        certificate.getSha256FingerprintBytes(), newCertificate.getSha256FingerprintBytes());
    assertEquals(
        certificate.getNotAfterTime().getSeconds(), newCertificate.getNotAfterTime().getSeconds());
    assertEquals(
        certificate.getNotAfterTime().getNanos(), newCertificate.getNotAfterTime().getNanos());
  }

  @Override
  @Test
  void parser_fromByteArray() throws InvalidProtocolBufferException {
    Certificate certificate =
        Certificate.newBuilder()
            .setIssuer("Issuer")
            .setParsed(false)
            .setSha256Fingerprint("SHA256")
            .setNotAfterTime(Timestamp.newBuilder().setSeconds(50).setNanos(100).build())
            .build();

    Certificate result = (Certificate) PreSplit.parserFromByteArray(certificate);
    assertEquals(result.getIssuer(), certificate.getIssuer());
    assertEquals(result.getParsed(), certificate.getParsed());
    assertEquals(result.getSha256FingerprintBytes(), certificate.getSha256FingerprintBytes());
    assertEquals(result.getNotAfterTime().getSeconds(), certificate.getNotAfterTime().getSeconds());
    assertEquals(result.getNotAfterTime().getNanos(), certificate.getNotAfterTime().getNanos());
  }

  @Override
  @Test
  void message_clear() {
    Certificate certificate =
        Certificate.newBuilder()
            .setIssuer("Issuer")
            .setParsed(false)
            .setSha256Fingerprint("SHA256")
            .setNotAfterTime(Timestamp.newBuilder().setSeconds(50).setNanos(100).build())
            .build();
    Certificate resetCertificate = (Certificate) PreSplit.messageClear(certificate);
    assertEquals("", resetCertificate.getIssuer());
    assertEquals(false, resetCertificate.getParsed());
    assertEquals("", resetCertificate.getSha256Fingerprint());
    assertEquals(0, resetCertificate.getNotAfterTime().getSeconds());
    assertEquals(0, resetCertificate.getNotAfterTime().getNanos());
  }
}
