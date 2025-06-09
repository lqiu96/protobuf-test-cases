#!/bin/sh

rm -rf tmp/
mkdir tmp
pushd tmp

# Install protobuf-api and protobuf-sdk locally
git clone https://github.com/blakeli0/protobuf-poc-split-keep-package.git
pushd protobuf-poc-split-keep-package

# Install the latest stable branch as (version: 1.0-SNAPSHOT)
git checkout latest
mvn clean install -T 1C

# Install the runtime breaking change in protobuf-sdk (version: runtime-only-error)
git checkout runtime-only-breaking-change
mvn clean install -T 1C

# Install the protoc protobuf runtime breaking changes in protobuf-sdk (version: protoc-runtime-breaking-change)
git checkout protoc-runtime-breaking-change
mvn clean install -T 1C
popd

# Install gax and core with protobuf-java shaded (version: protobuf-shading-within-module)
git clone https://github.com/googleapis/sdk-platform-java.git --depth=1
pushd sdk-platform-java
git checkout protobuf-shading-within-module
mvn clean install -T 1C -DskipTests -Dflatten.skip -Dclirr.skip -Denforcer.skip
popd

# Install kms, speech, and secretmanager with the shaded modules (version: protobuf-shading-within-module)
git clone https://github.com/googleapis/google-cloud-java.git --depth=1
pushd google-cloud-java
git checkout protobuf-shading-within-module
mvn clean install -T 1C -Dflatten.skip -DskipTests -Dcheckstyle.skip -Dclirr.skip -Denforcer.skip -pl 'java-kms/google-cloud-kms,java-speech/google-cloud-speech,java-secretmanager/google-cloud-secretmanager' -am

# Install kms with protobuf-sdk shaded with breaking change (version: runtime-only-error)
git checkout protobuf-shading-runtime-breaking-change-within-module
mvn clean install -T 1C -Dflatten.skip -DskipTests -Dcheckstyle.skip -Dclirr.skip -Denforcer.skip -pl 'java-kms/google-cloud-kms' -am

# Install kms with protobuf-sdk shaded with breaking change (version: protoc-runtime-breaking-change)
git checkout protoc-runtime-breaking-change
mvn clean install -T 1C -Dflatten.skip -DskipTests -Dcheckstyle.skip -Dclirr.skip -Denforcer.skip -pl 'java-kms/google-cloud-kms' -am
popd

popd
rm -rf tmp/
