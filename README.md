This repo is a multi-module maven project to test the impact of Protobuf-Java with the [split Protobuf repo](https://github.com/blakeli0/protobuf-poc-split-keep-package).

# How to run
## Pre-Reqs (Read this section if there are any unexpected issues)
- Java 17 (Install via https://sdkman.io/)
- Maven (Can be installed via https://sdkman.io/)

## Steps
1. A `setup.sh` shell script can be run to pull in and download the required Maven artifacts.
The script will do a few things
- Pull java-shared-configs and install the artifacts
- Pull the Protobuf-Java split repo artifacts and install them 
- Pull sdk-platform-java repo and install the Java SDK runtime artifacts. These artifacts have protobuf-java shaded and
have protobuf-api as their dependencies 
- Pull google-cloud-java and install a few select handwritten libraries with protobuf shaded. The modules that are shaded 
are kms, speech, and secretmanager as they cover a breadth of common requests in the java-sdk

Note: If you do not wish to setup a Google Cloud project, you can skip the following steps and run `mvn clean install`.
This will only run a subset of tests. If you want to run all the tests, continue with the following steps.

2. After running the setup script, set the env vars listed in [here](#env-vars)

3. The following Google Cloud services will need to have been activated and enabled in your Google Cloud Project:
- KMS
- Speech
- SecretManager
- Notebook

These APIs can be enabled via: https://cloud.google.com/service-usage/docs/enable-disable

4. Once all the above is configured, run `mvn clean install -Pall` to run through all the test cases.

# Modules
The following modules are configured to test a matrix of potential customer environments.
1. shading: Tests that shading inside the Java SDK is not impacted by customer's dependencies or impacts existing customer
code. The protobuf-sdk version brought in by the client library is isolated to within the library itself.
2. customer-protos: Tests customers who have their own gen code in addition to the Java SDK. These customers would have
their own protobuf versions defined to ensure compatibility with their gen code
3. java-sdk: Tests customers who simply use the Java-SDK to make requests and get responses. These customers rely on the
protobuf versions defined by the Java SDK
4. third-party-library: Test customers who have third party libraries that bring in protobuf. These customers may define
their own protobuf versions or may have a protobuf version defined by one of their dependencies

Additionally, protobuf-library is a module that simulates a third party dependency with different versions of protobuf
being brought in. This module is imported by `third-party-library` to simulate a library with protobuf.

# Maven Profiles
There are two Maven profiles that can be called with these test cases
1. local (active by default): Runs the tests that do not require an active Google Cloud project
2. all: Runs all tests in the repo (superset of local). This requires additional env vars to be set

## Env Vars
Additional env vars are required to run for calls to Google Cloud
- PROJECT_ID: This is your Google Cloud Project ID (i.e. `my-test-project`)
- LOCATION: This will be the region that your requests will hit (i.e. `us-central1`)
- ZONE: This is the zone that your requests will use (i.e. `us-central1-a`)

You can set these Env Vars by running the example command: `export PROJECT_ID={test_project_name}`
