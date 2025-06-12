/**
 * This interface declares the common test cases that will hit Google Cloud.
 * This will define the common cases for the modules to test. Adding new test
 * cases here requires all downstream modules to implement and prevents forgetting
 * the cases downstream.
 * </p>
 * The downstream modules implement the functionality as the downstream modules bring
 * in different version of protobuf for both compilation and runtime. Defining the
 * functionality here would require compiling with a specific version of protobuf
 * and would interfere with the intended compilation/runtime tests.
 */
interface BaseJavaSdkTestCases {

  // Basic implementation of request/response
  void kms_list();

  // Custom RPC that is not CRUD
  void speech_recognize();

  // Covers the common CRUD operations
  void secret_manager_CRUD();

  // Covers LROs
  void notebook_operations();
}
