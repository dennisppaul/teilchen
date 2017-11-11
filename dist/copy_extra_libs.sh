#!/bin/sh

LIB_NAME=$1
EXTRA_LIB_NAME=$2
ROOT=$(pwd)

SRC=$ROOT/../lib/$EXTRA_LIB_NAME
DST=$ROOT/../processing-library/$LIB_NAME/library/

echo "# lib '"$EXTRA_LIB_NAME"'"
cp "$SRC" "$DST"
