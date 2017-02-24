#!/bin/sh

LIB_NAME=$1
ROOT=$(pwd)

SRC=$ROOT/../README.md
DST=$ROOT/../processing-library/$LIB_NAME

cp "$SRC" "$DST"
