 #!/bin/bash

count=0

printstep() {
  count=`expr $count + 1`
  echo ................................................................................................................................
  echo "Step $count: $*"
  echo ................................................................................................................................
}

die() {
  echo "$*" >&2
  exit 1
}

currDir=${PWD##*/}  

printstep build keynote
gradle clean build
gradle distZip

printstep unzip 
pushd .
cd build/distributions/
unzip keynote-1.0.zip 
popd

printstep copy config
cp -r ./config/ ./build/distributions/keynote-1.0/bin/config

cd build/distributions/keynote-1.0/bin/
