#!/bin/sh

LIB_NAME=$1
ZIP_NAME=../$LIB_NAME.zip

find .. -name ".DS_Store" -print0 | xargs -0 rm -f
cd ../processing-library/

if [ -f "$ZIP_NAME" ]; then
	echo "# deleting existing zip file"
	rm "$ZIP_NAME"
fi

zip --quiet -r "$ZIP_NAME" ./$LIB_NAME
cd ../dist/