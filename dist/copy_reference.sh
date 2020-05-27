#!/bin/sh

LIB_NAME=$1
ROOT=$(pwd)

SRC=$ROOT/../reference
DST=$ROOT/../processing-library/$LIB_NAME/

cp -r "$SRC" "$DST"
