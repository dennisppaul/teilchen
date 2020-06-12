#!/bin/sh

source config.build

ROOT=$(pwd)

if [[ "$TERM" != "dumb" ]]; then
    C0=$(tput sgr0)
    C1=$(tput setaf $(expr $BASE_COLOR + 72))
    C2=$(tput setaf $BASE_COLOR)
fi

printJob()
{
	echo ""
	echo $C2"#########################################"
	echo $C2"# "$C1$1
	echo $C2"#########################################"
	echo $C0
}

printJob "create folder"
sh $ROOT/create-folder.sh $LIB_NAME
printJob "copying jar"
sh $ROOT/copy_jar.sh $LIB_NAME
printJob "copying additional libs"
for i in ${ADDITIONAL_LIBS[@]}; do
	sh $ROOT/copy_additional_libs.sh $LIB_NAME $i
done
printJob "copying src"
sh $ROOT/copy_src.sh $LIB_NAME
printJob "copying README"
sh $ROOT/copy_readme.sh $LIB_NAME
printJob "copying reference"
sh $ROOT/copy_reference.sh $LIB_NAME
printJob "creating processing sketches"
for i in ${IO_EXAMPLE_PATHS[@]}; do
	sh $ROOT/create-processing-sketches.sh $LIB_NAME $i
done
printJob "packing zip"
sh $ROOT/pack-zip.sh $LIB_NAME
printJob "done"
