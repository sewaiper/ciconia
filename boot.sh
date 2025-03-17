#!/bin/bash

JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
TDLIB=~/td
PROJECT_ROOT=~/ciconia

cd $TDLIB

rm -rf build
mkdir build
cd build

cmake -DCMAKE_BUILD_TYPE=Release \
      -DJAVA_HOME=$JAVA_HOME \
      -DOPENSSL_ROOT_DIR=/opt/homebrew/opt/openssl/ \
      -DCMAKE_INSTALL_PREFIX:PATH=../example/java/td \
      -DTD_ENABLE_JNI=ON ..

cmake --build . --target install

cd $PROJECT_ROOT
rm -rf tdlib
mkdir tdlib

cd "${TDLIB}/example/java"
rm -rf build
mkdir build
cd build

cmake -DCMAKE_BUILD_TYPE=Release \
      -DJAVA_HOME=$JAVA_HOME \
      -DOPENSSL_ROOT_DIR=/opt/homebrew/opt/openssl/ \
      -DCMAKE_INSTALL_PREFIX:PATH="${PROJECT_ROOT}/tdlib" \
      -DTd_DIR:PATH=$(greadlink -e ../td/lib/cmake/Td) ..

cmake --build . --target install

cd "${PROJECT_ROOT}/tdlib"

rm -rf jarRoot
mkdir jarRoot
cp -r ./bin/org ./jarRoot

cd jarRoot
jar cvf ../tdlib.jar *

cd $PROJECT_ROOT

mvn install:install-file -DlocalRepositoryPath=repo \
                         -DcreateChecksum=true \
                         -Dpackaging=jar \
                         -Dfile=tdlib/tdlib.jar \
                         -DgroupId=org.drinkless \
                         -DartifactId=tdlib \
                         -Dversion=1.0

rm -rf "${PROJECT_ROOT}/tdlib/jarRoot"
rm -f "${PROJECT_ROOT}/tdlib/tdlib.jar"

cd "${PROJECT_ROOT}/tdlib"

rm -rf docsRoot
mkdir docsRoot
cp -r ./docs/org ./docsRoot

cd docsRoot
jar cvf ../docs.jar *

cd $PROJECT_ROOT

mvn install:install-file -DlocalRepositoryPath=repo \
                         -DcreateChecksum=true \
                         -Dpackaging=jar \
                         -Dfile=tdlib/docs.jar \
                         -DgroupId=org.drinkless \
                         -DartifactId=tdlib \
                         -Dversion=1.0 \
                         -Dclassifier=javadoc

rm -rf "${PROJECT_ROOT}/tdlib/docsRoot"
rm -f "${PROJECT_ROOT}/tdlib/docs.jar"


