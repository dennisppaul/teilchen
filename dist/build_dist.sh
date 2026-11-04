#!/usr/bin/env bash

# ROOT=$(pwd)
ROOT=$(dirname "$0")

set -e
cd "$ROOT"
source config.build

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

JAR=../lib/$LIB_NAME.jar
if [ ! -f "$JAR" ]; then
	echo "+++ ERROR: '$JAR' does not exist. build it first ( e.g with 'release.sh <version> --build-only' )"
	exit 1
fi
if [ -n "$(find ../src -name '*.java' -newer "$JAR" -print -quit)" ]; then
	echo "+++ WARNING: '$JAR' is older than the sources in 'src' ( rebuild with 'release.sh <version> --build-only' )"
fi

printJob "create folder"
bash create-folder.sh $LIB_NAME
printJob "copying jar"
bash copy_jar.sh $LIB_NAME
printJob "copying additional libs"
for i in ${ADDITIONAL_LIBS[@]}; do
	bash copy_additional_libs.sh $LIB_NAME $i
done
printJob "copying src"
bash copy_src.sh $LIB_NAME
printJob "copying README"
bash copy_readme.sh $LIB_NAME
printJob "copying reference"
bash copy_reference.sh $LIB_NAME
printJob "creating processing sketches"
for i in ${IO_EXAMPLE_PATHS[@]}; do
	bash create-processing-sketches.sh $LIB_NAME $i
done
printJob "packing zip"
bash pack-zip.sh $LIB_NAME
printJob "done"
