import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Base abstract class for the Third Party Dependency test cases.
 */
public abstract class BaseTestHelper {

  protected static File tempCertificateFile;
  protected static Path certificatePartialPath;

  protected static final String PARTIAL_ISSUER = "randomIssuer";
  protected static final boolean PARTIAL_PARSED = true;
  protected static final String PARTIAL_SHA256 = "randomSHA256";
  protected static final int PARTIAL_SECONDS = 1234;
  protected static final int PARTIAL_NANOS = 5678;

  @BeforeAll
  static void setup() throws IOException {
    tempCertificateFile = File.createTempFile("certificate", null);
    certificatePartialPath = Paths.get("src", "test", "resources", "certificate_partial.txt");

    File certificatePartialFile = certificatePartialPath.toFile();
    // Create the directories if it doesn't already exist
    certificatePartialFile.getParentFile().mkdirs();
  }

  @AfterAll
  static void cleanUp() {
    tempCertificateFile.delete();
  }

  /**
   * The following methods are added as abstract methods to ensure that all
   * module test cases scenarios (compiled with version X and run with version Y).
   */

  @Test
  abstract void kms_list();

  @Test
  abstract void speech_recognize();

  @Test
  abstract void secret_manager_CRUD();

  @Test
  abstract void notebook_operations();

  @Test
  abstract void mergeFrom() throws IOException;

  @Test
  abstract void writeToFile_readFromFile() throws IOException;

  @Test
  abstract void parser_fromByteArray() throws IOException;

  @Test
  abstract void message_clear() throws IOException;
}
