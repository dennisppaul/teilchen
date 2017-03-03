#!/bin/sh

LIB_NAME=$1
ROOT=$(pwd)
DST=$ROOT/../processing-library/$LIB_NAME

if [ -d "$DST" ]; then
	echo "# deleting existing folder"
	rm -rf "$DST"
fi

mkdir -p "$DST"
