#!/bin/sh

LIB_NAME=$1
ROOT=$(pwd)

SRC=$ROOT/../lib/$LIB_NAME.jar
DST=$ROOT/../processing-library/$LIB_NAME/library/

if [ -d "$DST" ]; then
	rm -rf "$DST"
fi
mkdir -p "$DST"

cp "$SRC" "$DST"
