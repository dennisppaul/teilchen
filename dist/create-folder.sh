#!/bin/sh

LIB_NAME=$1
ROOT=$(pwd)
DST=$ROOT/../processing-library/$LIB_NAME

if [ -d "$DST" ]; then
	rm -rf "$DST"
fi

mkdir -p "$DST"
