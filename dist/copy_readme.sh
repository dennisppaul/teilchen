#!/bin/sh

LIB_NAME=$1
ROOT=$(pwd)

SRC=$ROOT/../README.md
SRC_LICENSE=$ROOT/../LICENSE
DST=$ROOT/../processing-library/$LIB_NAME

cp "$SRC" "$DST"
cp "$SRC_LICENSE" "$DST"
