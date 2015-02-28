#!/bin/sh

#
# for further hints on `sed` read this: http://www.grymoire.com/Unix/Sed.html
#

SRC_PATH="../src/teilchen/additional/examples"
OUTPUT_DIR="../processing-library/teilchen/examples"

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
			# remove package
			s/package.*//
			# remove processing imports
			s/import processing.core.*//
			# remove class defintion 
			s/.*extends PApplet {//
			# remove all tabs from line start
			s/[ ^I]*$//
			# remove 'private' + 'protected' + 'public'
			s/private //
			s/protected //
			s/public //
			# remove main method
			/static void main/,/}$/ {
				D
			}
			# remove last line
			/^}/ d
			# remove trailing space
			s/    //
		'\
		> /tmp/tmp.pde

		cat /tmp/tmp.pde | \
		sed '
			1 i\
			 import mathematik.*;
			# remove empty lines
			#/^$/ d
			/^$/{N;/^\n$/d;}
		'\
		> $OUTPUT_DIR/$SKETCHNAME/$SKETCHFILE_NAME
done

exit 0

#  ---- left overs ---- #
# 
# 	cat $file | \
# 	sed '
# 			# only consider the lines in 'PApplet'
# 			/extends PApplet/,/^}$/ !d
# 			# remove all tabs from line start
# 			s/[ ^I]*$//
# 			# remove empty lines
# 			/^$/ d
# 			# remove 'private' + 'protected' + 'public'
# 			s/private //
# 			s/protected //
# 			s/public //
# 			# remove main method
# 			/static void main/,/}$/ {
# 				D
# 			}
# 			# remove first and last line
# 			/^class/ d
# 			/^}/ d
# 			# remove trailing space
# 			s/    //
		'\
		> /tmp/tmp.pde

		cat /tmp/tmp.pde | \
		sed '
			1 i\
			 import ciid2015.exquisitdatacorpse.*;\
			 import oscP5.*;\
			 import netP5.*;\
			 \
		'\
		> $OUTPUT_DIR/$SKETCHNAME/$SKETCHFILE_NAME
