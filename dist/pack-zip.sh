#!/bin/sh

LIB_NAME=$1
find .. -name ".DS_Store" -print0 | xargs -0 rm -f
cd ../processing-library/
if [ -d "../$LIB_NAME.zip" ]; then
	rm "../$LIB_NAME.zip"
fi
zip --quiet -r ../$LIB_NAME.zip ./$LIB_NAME
cd ../dist/