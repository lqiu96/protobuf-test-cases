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
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class CustomerProtosPreSplitRuntimeTest {

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
