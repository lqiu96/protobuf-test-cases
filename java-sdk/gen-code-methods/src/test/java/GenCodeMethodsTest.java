import com.google.cloud.kms.v1.Certificate;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GenCodeMethodsTest {

  private static File certificateFile;

  @BeforeAll
  static void setup() throws IOException {
    certificateFile = File.createTempFile("certificate", null);
  }

  @AfterAll
  static void cleanUp() {
    certificateFile.delete();
  }

  @Test
  void mergeFrom() throws IOException {
    // Issuer: randomIssuer, Parsed: true, Sha256FingerPrint: randomSHA256, NotAfterTimestamp: 1234 sec, 5678 nanos
    Path path = Paths.get("src", "test", "resources","certificate_partial.txt");

    Certificate.Builder certificateBuilder = Certificate.newBuilder();
    certificateBuilder.mergeFrom(new FileInputStream(path.toFile()));

    Certificate certificate = certificateBuilder.build();
    assertEquals("randomIssuer", certificate.getIssuer());
    assertEquals(true, certificate.getParsed());
    assertEquals("randomSHA256", certificate.getSha256Fingerprint());
    assertEquals(1234, certificate.getNotAfterTime().getSeconds());
    assertEquals(5678, certificate.getNotAfterTime().getNanos());
  }

  @Test
  void writeToFile_readFromFile() throws IOException {
    Certificate certificate = Certificate.newBuilder()
            .setIssuer("Issuer")
            .setParsed(false)
            .setSha256FingerprintBytes(ByteString.copyFrom("SHA256", StandardCharsets.UTF_8))
            .setNotAfterTime(Timestamp.newBuilder().setSeconds(50).setNanos(100).build())
            .build();

    try (FileOutputStream outputStream = new FileOutputStream(certificateFile)) {
      certificate.writeTo(outputStream);
    }

    Certificate newCertificate;
    try (FileInputStream inputStream = new FileInputStream(certificateFile)) {
      newCertificate = Certificate.parseFrom(inputStream);
    }
    assertEquals(certificate.getIssuer(), newCertificate.getIssuer());
    assertEquals(certificate.getParsed(), newCertificate.getParsed());
    assertEquals(certificate.getSha256FingerprintBytes(), newCertificate.getSha256FingerprintBytes());
    assertEquals(certificate.getNotAfterTime().getSeconds(), newCertificate.getNotAfterTime().getSeconds());
    assertEquals(certificate.getNotAfterTime().getNanos(), newCertificate.getNotAfterTime().getNanos());
  }

  @Test
  void parser_fromByteArray() throws InvalidProtocolBufferException {
    Certificate certificate = Certificate.newBuilder()
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
    Certificate certificate = Certificate.newBuilder()
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
