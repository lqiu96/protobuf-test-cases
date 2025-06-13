import com.google.protobuf.InvalidProtocolBufferException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * This abstract class is the base class for testing more advanced protobuf functionality. This
 * covers setting up any required files to read/write for parsing as well as merging.
 */
public abstract class BaseAdvancedUseCaseTestCases {

  protected static File tempFile;
  protected static Path partialPath;

  // Certificate constants (KMS)
  protected static final String PARTIAL_ISSUER = "randomIssuer";
  protected static final boolean PARTIAL_PARSED = true;
  protected static final String PARTIAL_SHA256 = "randomSHA256";
  protected static final int PARTIAL_SECONDS = 1234;
  protected static final int PARTIAL_NANOS = 5678;

  // Book constants (customer proto)
  protected static final int PARTIAL_ISBN = 9999;
  protected static final String PARTIAL_TITLE = "myNewTitle";
  protected static final String PARTIAL_AUTHOR = "myNewAuthor";

  @BeforeAll
  static void setup() throws IOException {
    tempFile = File.createTempFile("temp", null);
    partialPath = Paths.get("src", "test", "resources", "partial.txt");

    File partialPathFile = partialPath.toFile();
    // Create the directories if it doesn't already exist
    partialPathFile.getParentFile().mkdirs();
  }

  @AfterAll
  static void cleanUp() {
    tempFile.delete();
  }

  @Test
  abstract void mergeFrom() throws IOException;

  @Test
  abstract void writeToFile_readFromFile() throws IOException;

  @Test
  abstract void parser_fromByteString() throws InvalidProtocolBufferException;

  @Test
  abstract void parser_fromByteArray() throws IOException;

  @Test
  abstract void message_clear() throws IOException;

  @Test
  abstract void fieldDescriptors_get_set();
}
