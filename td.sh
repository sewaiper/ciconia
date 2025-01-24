#!/bin/bash

JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
TDLIB=/Users/dmitry.chernov/Documents/Projects/news/td
TG_NEWS=/Users/dmitry.chernov/Documents/Projects/news/tg-news

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

cd $TG_NEWS
rm -rf tdlib
mkdir tdlib

cd "${TDLIB}/example/java"
rm -rf build
mkdir build
cd build

cmake -DCMAKE_BUILD_TYPE=Release \
      -DJAVA_HOME=$JAVA_HOME \
      -DOPENSSL_ROOT_DIR=/opt/homebrew/opt/openssl/ \
      -DCMAKE_INSTALL_PREFIX:PATH="${TG_NEWS}/tdlib" \
      -DTd_DIR:PATH=$(greadlink -e ../td/lib/cmake/Td) ..

cmake --build . --target install

cd "${TG_NEWS}/tdlib"

rm -rf ./bin/org/drinkless/tdlib/example

rm -rf jarRoot
mkdir jarRoot
cp -r ./bin/org ./jarRoot

cd jarRoot
jar cvf ../tdlib.jar *

cd $TG_NEWS

mvn install:install-file -DlocalRepositoryPath=repo \
                         -DcreateChecksum=true \
                         -Dpackaging=jar \
                         -Dfile=tdlib/tdlib.jar \
                         -DgroupId=org.drinkless \
                         -DartifactId=tdlib \
                         -Dversion=1.0

rm -rf jarRoot
rm -rf tdlib.jar
