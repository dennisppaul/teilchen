#!/bin/sh

LIB_NAME=$1
ADDITIONAL_LIB_NAME=$2
ROOT=$(pwd)

SRC=$ROOT/../lib/$ADDITIONAL_LIB_NAME
DST=$ROOT/../processing-library/$LIB_NAME/library/

echo "# lib '"$ADDITIONAL_LIB_NAME"'"
cp "$SRC" "$DST"
