#!/bin/sh

SRC=$1/../src
DST=$1/../processing-library/teilchen/

if [ -d "$DST/src" ]; then
	rm -rf "$DST/src"
fi

cp -r "$SRC" "$DST"
