#!/bin/sh

LIB_NAME=$1
ROOT=$(pwd)

SRC=$ROOT/../src
DST=$ROOT/../processing-library/$LIB_NAME

if [ -d "$DST/src" ]; then
	rm -rf "$DST/src"
fi
mkdir -p "$DST"

cp -r "$SRC" "$DST"
