#!/bin/sh

rm -rf tmp/
mkdir tmp
pushd tmp

# Install protobuf-api and protobuf-sdk locally
git clone https://github.com/blakeli0/protobuf-poc-split-keep-package.git
pushd protobuf-poc-split-keep-package
git checkout latest
mvn clean install -T 1C
popd

# Install gax and core with protobuf-java shaded
git clone https://github.com/googleapis/sdk-platform-java.git --depth=1
pushd sdk-platform-java
git checkout protobuf-shading-within-module
mvn clean install -T 1C -DskipTests -Dflatten.skip -Dclirr.skip
popd

# Install kms, speech, and secretmanager with the shaded modules
git clone https://github.com/googleapis/google-cloud-java.git --depth=1
pushd google-cloud-java
git checkout protobuf-shading-within-module
mvn clean install -T 1C -Dflatten.skip -DskipTests -Dcheckstyle.skip -Dclirr.skip -pl 'java-kms/google-cloud-kms,java-speech/google-cloud-speech,java-secretmanager/google-cloud-secretmanager' -am
popd

popd
rm -rf tmp/
