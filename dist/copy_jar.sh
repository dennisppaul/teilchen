#!/bin/sh

SRC=$1/../../../netbeans/dist/teilchen.jar
DST=$1/../processing-library/teilchen/library

if [ -d "$DST" ]; then
	rm -rf "$DST"
fi
mkdir -p "$DST"

cp "$SRC" "$DST"