#!/bin/sh

source config.build

ROOT=$(pwd)

printJob()
{
	echo ""
	echo "################################"
	echo "# "$1
	echo "################################"
}

printJob "create folder"
sh $ROOT/create-folder.sh $LIB_NAME
printJob "copying jar"
sh $ROOT/copy_jar.sh $LIB_NAME
printJob "copying src"
sh $ROOT/copy_src.sh $LIB_NAME
printJob "creating processing sketches"
for i in ${IO_EXAMPLE_PATHS[@]}; do
	sh $ROOT/create-processing-sketches.sh $LIB_NAME $i
done
printJob "packing zip"
sh $ROOT/pack-zip.sh $LIB_NAME
printJob "done"
