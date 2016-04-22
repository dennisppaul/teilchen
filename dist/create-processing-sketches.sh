#!/bin/sh

# for further hints on `sed` read this: http://www.grymoire.com/Unix/Sed.html

LIB_NAME=$1
INPUT_FOLDER=$2
OUTPUT_FOLDER=$2
SRC_PATH="../src/$LIB_NAME/$INPUT_FOLDER/"
OUTPUT_DIR="../processing-library/$LIB_NAME/$OUTPUT_FOLDER"

if [ -d "$OUTPUT_DIR" ]; then
	rm -rf "$OUTPUT_DIR"
fi
mkdir -p "$OUTPUT_DIR"

for file in $SRC_PATH/*.java
do
	#echo "$file"
	FILENAME=$(echo $file | sed -e 's/.*\///') # retreive filename
	SKETCHNAME=$(echo $FILENAME | sed -e 's/.java//')
	SKETCHNAME=$(echo $SKETCHNAME | sed -e 's/Sketch//')
	SKETCHFILE_NAME="$SKETCHNAME.pde"
	
	
	echo "# sketch '"$SKETCHNAME"'"

	mkdir -p $OUTPUT_DIR/$SKETCHNAME

	cat $file | \
	sed '
			# only consider the lines in 'PApplet'
			/extends PApplet/,/^}$/ !d
			# remove all tabs from line start
			s/[ ^I]*$//
			# remove empty lines
			/^$/ d
			# remove 'private' + 'protected' + 'public'
			s/private //
			s/protected //
			s/public //
			# remove main method
			/static void main/,/}$/ {
				D
			}
			# remove add-comment
			s/\/\/@add//
			# remove first and last line
			/^class/ d
			/^}/ d
			# remove trailing space
			s/    //
		'\
		> /tmp/tmp.pde

		cat /tmp/tmp.pde | \
		sed '
			1 i\
			 import '$LIB_NAME'.*;\
			 import '$LIB_NAME'.behavior.*;\
			 import '$LIB_NAME'.constraint.*;\
			 import '$LIB_NAME'.cubicle.*;\
			 import '$LIB_NAME'.force.*;\
			 import '$LIB_NAME'.integration.*;\
			 import '$LIB_NAME'.util.*;\
			 \
		'\
		> $OUTPUT_DIR/$SKETCHNAME/$SKETCHFILE_NAME

done
