#!/bin/sh

LIB_NAME=$1
find .. -name ".DS_Store" -print0 | xargs -0 rm -f
cd ../processing-library/
zip --quiet -r ../$LIB_NAME.zip ./$LIB_NAME
cd ../dist/