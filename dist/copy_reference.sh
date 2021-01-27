#!/bin/sh

LIB_NAME=$1
ROOT=$(pwd)

# update stylessheet from lib
CSS=stylesheet.css
SRC=$ROOT/../lib/$CSS
DST=$ROOT/../docs
cp "$SRC" "$DST"

# copy reference

SRC=$ROOT/../docs
DST=$ROOT/../processing-library/$LIB_NAME/reference

cp -r "$SRC" "$DST"
