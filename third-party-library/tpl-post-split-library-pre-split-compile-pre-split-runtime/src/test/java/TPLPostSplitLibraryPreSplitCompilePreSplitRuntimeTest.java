import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.google.cloud.kms.v1.Certificate;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Timestamp;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class TPLPostSplitLibraryPreSplitCompilePreSplitRuntimeTest extends BaseAdvancedUseCaseTestCases
    implements BaseJavaSdkTestCases {

  @Override
  @Test
  @Timeout(value = 5, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
  public void kms_list() {
    PostSplitLibraryPreSplitCompile.kmsList();
  }

  @Override
  @Test
  @Timeout(value = 5, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
  public void speech_recognize() {
    PostSplitLibraryPreSplitCompile.speechRecognize();
  }

  @Override
  @Test
  @Timeout(value = 5, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
  public void secret_manager_CRUD() {
    PostSplitLibraryPreSplitCompile.secretManagerCRUD();
  }

  @Override
  @Test
  @Timeout(value = 15, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
  public void notebook_operations() {
    PostSplitLibraryPreSplitCompile.notebooksOperations();
  }

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
    try (FileOutputStream outputStream = new FileOutputStream(partialPath.toFile())) {
      fileCertificate.writeTo(outputStream);
    }
    Certificate certificate =
        (Certificate)
            PostSplitLibraryPreSplitCompile.mergeFrom(Certificate.newBuilder(), partialPath);
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
        (Certificate)
            PostSplitLibraryPreSplitCompile.writeToFileReadFromFile(
                certificate, certificate.getParserForType());
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

    Certificate result =
        (Certificate) PostSplitLibraryPreSplitCompile.parserFromByteArray(certificate);
    assertEquals(result.getIssuer(), certificate.getIssuer());
    assertEquals(result.getParsed(), certificate.getParsed());
    assertEquals(result.getSha256FingerprintBytes(), certificate.getSha256FingerprintBytes());
    assertEquals(result.getNotAfterTime().getSeconds(), certificate.getNotAfterTime().getSeconds());
    assertEquals(result.getNotAfterTime().getNanos(), certificate.getNotAfterTime().getNanos());
  }

  @Test
  void parser_fromByteString() throws InvalidProtocolBufferException {
    Certificate certificate =
        Certificate.newBuilder()
            .setIssuer("Issuer")
            .setParsed(false)
            .setSha256Fingerprint("SHA256")
            .setNotAfterTime(Timestamp.newBuilder().setSeconds(50).setNanos(100).build())
            .build();

    Certificate result = Certificate.parser().parseFrom(certificate.toByteString());
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
    Certificate resetCertificate =
        (Certificate) PostSplitLibraryPreSplitCompile.messageClear(certificate);
    assertEquals("", resetCertificate.getIssuer());
    assertFalse(resetCertificate.getParsed());
    assertEquals("", resetCertificate.getSha256Fingerprint());
    assertEquals(0, resetCertificate.getNotAfterTime().getSeconds());
    assertEquals(0, resetCertificate.getNotAfterTime().getNanos());
  }

  @Test
  void fieldDescriptors_get_set() {
    Certificate.Builder builder = Certificate.newBuilder();
    Descriptors.Descriptor descriptor = builder.getDescriptorForType();

    Descriptors.FieldDescriptor issuer = descriptor.findFieldByName("issuer");
    builder.setField(issuer, "myIssuer");

    Descriptors.FieldDescriptor parsed = descriptor.findFieldByName("parsed");
    builder.setField(parsed, false);

    Descriptors.FieldDescriptor sha256Fingerprint =
        descriptor.findFieldByName("sha256_fingerprint");
    builder.setField(sha256Fingerprint, ByteString.copyFrom("SHA256", StandardCharsets.UTF_8));

    Descriptors.FieldDescriptor notAfterTime = descriptor.findFieldByName("not_after_time");
    builder.setField(notAfterTime, Timestamp.newBuilder().setSeconds(1234).setNanos(5678).build());

    Certificate certificate = builder.build();
    assertEquals("myIssuer", certificate.getIssuer());
    assertFalse(certificate.getParsed());
    assertEquals("SHA256", certificate.getSha256Fingerprint());
    assertEquals(1234, certificate.getNotAfterTime().getSeconds());
    assertEquals(5678, certificate.getNotAfterTime().getNanos());
  }
}
