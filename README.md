This repo is a multi-module maven project to test the impact of Protobuf-Java with the split Protobuf repo.

# How to run
A convenient `setup.sh` script can be run to pull in and download the required Maven artifacts.
The script will do three things:
1. Pull the Protobuf-Java split repo artifacts and install them
2. Pull sdk-platform-java repo and install the Java SDK runtime artifacts. These artifacts have protobuf-java shaded and
have protobuf-api as their dependencies
3. Pull google-cloud-java and install a few select handwritten libraries with protobuf shaded. The modules that are shaded 
are kms, speech, and secretmanager as they cover a breadth of common requests in the java-sdk 

After running the setup script, run `mvn test` to run through all the test cases.

# Modules
The following modules are configured to test a matrix of potential customer environments
1. customer-protos: Tests customers who have their own gen code in addition to the Java SDK. These customers would have
their own protobuf versions defined to ensure compatibility with their gen code
2. java-sdk: Tests customers who simply use the Java-SDK to make requests and get responses. These customers rely on the
protobuf versions defined by the Java SDK
3. third-party-library: Test customers who have third party libraries that bring in protobuf. These customers may define
their own protobuf versions or may have a protobuf version defined by one of their dependencies

Additionally, protobuf-library is a module that simulates a third party dependency with different versions of protobuf
being brought in. This module is imported by `third-party-library` to simulate a library with protobuf.