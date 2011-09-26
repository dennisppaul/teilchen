#!/bin/bash

echo "creating distribution version "$1

TEMP_DIR=../../tmp
WORK_DIR=..
DIST_DIR=$TEMP_DIR/dist
PRODUCT_NAME=particles.zip

#
# create working directories
#
mkdir $TEMP_DIR
mkdir $DIST_DIR

#
# copy files to temp
#
echo "copying files."
cp -R $WORK_DIR/src $TEMP_DIR
cp -R $WORK_DIR/resources $TEMP_DIR
cp LICENSE $TEMP_DIR

#
# removing junk
#
echo "removing junk."
find $TEMP_DIR/. -name '.DS_Store' -print0 | xargs -0 -n1 rm -f
find $TEMP_DIR/. -name '.svn' -print0 | xargs -0 -n1 rm -rf

#
# zipping sources
#
echo "zipping sources."
cd $TEMP_DIR
zip -r -q ./dist/$PRODUCT_NAME ./src
zip -r -q ./dist/$PRODUCT_NAME ./resources
zip -q ./dist/$PRODUCT_NAME ./LICENSE
cd -

#
# cleaning up
#
cp $DIST_DIR/$PRODUCT_NAME ../..
rm -rf $TEMP_DIR

#
#
#
#
exit
#
#
#
#

# create doc
javadoc -classpath . -quiet -d ../tmp/gestalt-doc/ -sourcepath ../tmp/src -linksource -subpackages gestalt mathematik werkzeug -exclude gestalt.demo

# make doc zip
echo "creating 'gestalt-doc.zip'"
cd ../tmp
zip -r ./dist/gestalt-doc.zip ./gestalt-doc
rm -rf ./gestalt-doc
cd -

# processing zip
mkdir ../tmp/processing
mkdir ../tmp/processing/gestalt_p5
mkdir ../tmp/processing/gestalt_p5/library
mkdir ../tmp/processing/gestalt_p5/examples
cp ../jar/gestalt.jar ../tmp/processing/gestalt_p5/library
cp ../jar/gestalt_p5.jar ../tmp/processing/gestalt_p5/library
cp README-PROCESSING ../tmp/processing/gestalt_p5
cp DISTRIBUTION ../tmp/processing/gestalt_p5
cp LICENSE ../tmp/processing/gestalt_p5
cp LICENSE-NANOXML ../tmp/processing/gestalt_p5
cp -R ../tmp/pde_examples/ ../tmp/processing/gestalt_p5/examples
cd ../tmp
zip -r ./dist/gestalt-processing.zip ./processing
cd -

# create data jar
./create_data_jar.sh

# copy jars
cp ../jar/*.jar ../tmp/dist
rm ../tmp/dist/gestalt_p5.jar

# bundle everything
cd ../tmp
mv ./dist "gestalt-dist-"$1
zip -r "gestalt-dist-"$1".zip" "gestalt-dist-"$1
mv "gestalt-dist-"$1".zip" "../gestalt-dist-"$1".zip"
cd -

# clean up
rm -rf ../tmp
