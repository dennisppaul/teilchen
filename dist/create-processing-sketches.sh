#!/bin/sh

source config.build

# for further hints on `sed` read this: http://www.grymoire.com/Unix/Sed.html

LIB_NAME=$1
INPUT_FOLDER=$2
OUTPUT_FOLDER=$2
M_PACKAGE_FOLDER=$(echo $PROJECT_PACKAGE | sed -e 's/\./\//g')
SRC_PATH="../src/$M_PACKAGE_FOLDER/$INPUT_FOLDER/"
OUTPUT_DIR="../processing-library/$LIB_NAME/$OUTPUT_FOLDER"

if [ -d "$OUTPUT_DIR" ]; then
	rm -rf "$OUTPUT_DIR"
fi
mkdir -p "$OUTPUT_DIR"

# compile imports
M_IMPORTS='import '$PROJECT_PACKAGE'.*; \
'
for j in ${SKETCH_IMPORTS[@]}; do
	M_IMPORTS=$M_IMPORTS'import '$j'; \
'
done
M_IMPORTS=$M_IMPORTS'
'

M_NL='\
'

# transmogrify sketches
for file in $SRC_PATH/*.java
do
	#echo "$file"
	FILENAME=$(echo $file | sed -e 's/.*\///') # retrieve filename
	SKETCHNAME=$(echo $FILENAME | sed -e 's/.java//')
	SKETCHNAME=$(echo $SKETCHNAME | sed -e 's/Sketch//')
	SKETCHFILE_NAME="$SKETCHNAME.pde"
	
	echo "# sketch '"$SKETCHNAME"'"

	mkdir -p $OUTPUT_DIR/$SKETCHNAME

	cat $file | \
	sed '
			# only consider the lines in PApplet
			/extends PApplet/,/^}$/ !d
			# remove all tabs from line start
			s/[ ^I]*$//
			# remove empty lines
			/^$/ d
			# remove private + protected + public
			s/private //
			s/protected //
			s/public //
			# simplify generics
			s/new ArrayList<>()/new ArrayList()/
			# remove main method
			/static void main/,/}$/ {
				D
			}
			# remove add-comment
			s/\/\/@add\ //
			# remove @Override
			s/@Override//
			# remove formatter
			/\/\/\ @formatter\:/ d
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
'"$M_IMPORTS"''\
		> $OUTPUT_DIR/$SKETCHNAME/$SKETCHFILE_NAME

done
