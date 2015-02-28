#!/bin/sh

LIBRARY=teilchen
VERSION=$(printf %03d%s ${2%.*})
ZIP=$1/../dist/$LIBRARY-$VERSION.zip

echo "* packing "$LIBRARY-$VERSION.zip

if test -e "$ZIP"
	then echo "* removind old archive"; rm "$ZIP"
fi

find $1/.. -name ".DS_Store" -print0 | xargs -0 rm -f
cd $1/../processing-library/
zip --quiet -r $ZIP ./$LIBRARY
cd $1/../dist/