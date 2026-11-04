#!/usr/bin/env bash
#
# builds and publishes a teilchen release without the IDE:
#
#   1. sets the version in `lib/library.properties` + `teilchen.txt` and dates the release notes
#   2. compiles `src` into `lib/teilchen.jar` ( java 17 bytecode )
#   3. runs the headless tests ( `teilchen.test.TestIntegrators`, `teilchen.test.TestColliders`, `teilchen.test.TestGeometry` )
#   4. generates the reference ( javadoc ) into `docs`
#   5. runs `build_dist.sh` ( processing library folder + `teilchen.zip` )
#   6. commits, tags, pushes and creates a GitHub release with `teilchen.zip` + `teilchen.txt`
#
# usage: dist/release.sh <version> [options]
#
#   version         in the style `v0.5`
#   --no-push       build, commit and tag locally but do not push or create a GitHub release
#   --build-only    only build ( steps 1–5 ), no commit, tag or push. working tree may be dirty
#   --publish-only  push an existing local release commit + tag ( from --no-push ) and create the GitHub release
#   --skip-tests    do not run the tests
#   -y, --yes       do not ask before pushing
#
# environment:
#
#   PROCESSING_APP  path to Processing.app ( default: /Applications/Processing.app )
#   JAVA_HOME       JDK 17 or newer
#

set -euo pipefail

# ---------------------------------------------------------------------------------------------------------------------

usage() {
    sed -n '3,25p' "$0" | sed 's/^# \{0,1\}//'
    exit "${1:-0}"
}

log() {
    printf '\n\033[1m+++ %s\033[0m\n' "$*"
}

fail() {
    printf '\033[31m+++ ERROR: %s\033[0m\n' "$*" >&2
    exit 1
}

# ---------------------------------------------------------------------------------------------------------------------

VERSION=""
DO_PUSH=1
DO_GIT=1
DO_TESTS=1
ASSUME_YES=0
PUBLISH_ONLY=0

while [ $# -gt 0 ]; do
    case "$1" in
        --no-push) DO_PUSH=0 ;;
        --build-only) DO_GIT=0; DO_PUSH=0 ;;
        --publish-only) PUBLISH_ONLY=1 ;;
        --skip-tests) DO_TESTS=0 ;;
        -y|--yes) ASSUME_YES=1 ;;
        -h|--help) usage 0 ;;
        v*) VERSION="$1" ;;
        *) echo "+++ unknown argument: $1"; usage 1 ;;
    esac
    shift
done

[ -n "$VERSION" ] || { echo "+++ missing version"; usage 1; }
[[ "$VERSION" =~ ^v([0-9]+)\.([0-9]+)$ ]] || fail "version must look like \`v0.5\` ( got \`$VERSION\` )"
V_MAJOR="${BASH_REMATCH[1]}"
V_MINOR="${BASH_REMATCH[2]}"
PRETTY_VERSION="$V_MAJOR.$V_MINOR"
# processing contribution manager needs an increasing integer
LIB_VERSION=$((V_MAJOR * 100 + V_MINOR))
[ "$V_MAJOR" -eq 0 ] && LIB_VERSION="$V_MINOR"
RELEASE_DATE=$(date +%Y%m%d)

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

PROCESSING_APP="${PROCESSING_APP:-/Applications/Processing.app}"
if [ -n "${JAVA_HOME:-}" ]; then
    JAVAC="$JAVA_HOME/bin/javac"; JAVA="$JAVA_HOME/bin/java"; JAR="$JAVA_HOME/bin/jar"; JAVADOC="$JAVA_HOME/bin/javadoc"
else
    JAVAC=javac; JAVA=java; JAR=jar; JAVADOC=javadoc
fi

BUILD_DIR="$(mktemp -d "${TMPDIR:-/tmp}/teilchen-release.XXXXXX")"
trap 'rm -rf "$BUILD_DIR"' EXIT

# ---------------------------------------------------------------------------------------------------------------------

log "checking preconditions for $VERSION ( prettyVersion=$PRETTY_VERSION, version=$LIB_VERSION )"

for TOOL in "$JAVAC" "$JAVA" "$JAR" "$JAVADOC" zip git; do
    command -v "$TOOL" > /dev/null || fail "\`$TOOL\` not found"
done
JAVAC_MAJOR=$("$JAVAC" -version 2>&1 | sed -E 's/^javac ([0-9]+).*/\1/')
[ "$JAVAC_MAJOR" -ge 17 ] 2> /dev/null || fail "JDK 17 or newer required ( found \`$("$JAVAC" -version 2>&1)\` )"

CORE_JAR=$(ls "$PROCESSING_APP"/Contents/app/resources/core/library/core-*.jar 2> /dev/null | head -n 1 || true)
[ -f "$CORE_JAR" ] || fail "processing core jar not found in \`$PROCESSING_APP\` ( set PROCESSING_APP )"
CLASSPATH_BUILD="$CORE_JAR"

grep -qE "^## $VERSION( |$)" RELEASE_NOTES.md || fail "RELEASE_NOTES.md has no section \`## $VERSION\`"

if [ "$DO_GIT" -eq 1 ]; then
    [ -z "$(git status --porcelain)" ] || fail "working tree is not clean. commit or stash changes first ( or use --build-only )"
    if [ "$PUBLISH_ONLY" -eq 1 ]; then
        git rev-parse -q --verify "refs/tags/$VERSION" > /dev/null || fail "tag \`$VERSION\` does not exist. run without --publish-only first"
        [ "$(git rev-parse "$VERSION^{commit}")" = "$(git rev-parse HEAD)" ] || fail "tag \`$VERSION\` is not on HEAD"
        [ -f teilchen.zip ] || fail "teilchen.zip not found"
    else
        git rev-parse -q --verify "refs/tags/$VERSION" > /dev/null && fail "tag \`$VERSION\` already exists"
    fi
    BRANCH=$(git branch --show-current)
    [ -n "$BRANCH" ] || fail "not on a branch"
fi
if [ "$DO_PUSH" -eq 1 ]; then
    command -v gh > /dev/null || fail "\`gh\` ( GitHub CLI ) not found. install it or use --no-push"
    gh auth status > /dev/null 2>&1 || fail "\`gh\` is not logged in. run \`gh auth login\` or use --no-push"
    git fetch --quiet --tags origin
    git ls-remote --exit-code --tags origin "refs/tags/$VERSION" > /dev/null 2>&1 && fail "tag \`$VERSION\` already exists on origin"
    [ "$(git rev-parse HEAD)" = "$(git rev-parse "origin/$BRANCH" 2> /dev/null || echo none)" ] ||
        git merge-base --is-ancestor "origin/$BRANCH" HEAD 2> /dev/null ||
        fail "local \`$BRANCH\` is behind or diverged from \`origin/$BRANCH\`. pull first"
fi

# ---------------------------------------------------------------------------------------------------------------------

# pushes branch + tag and creates the GitHub release
publish() {
    # release date from `## v0.5 (20261104)`, so publishing on a later day keeps the release date
    local DATE_IN_NOTES
    DATE_IN_NOTES=$(sed -nE "s/^## $VERSION \(([0-9]{8})\).*/\1/p" RELEASE_NOTES.md | head -n 1)
    [ -n "$DATE_IN_NOTES" ] && RELEASE_DATE="$DATE_IN_NOTES"

    # release notes for this version only ( up to the next `## ` heading )
    awk -v v="$VERSION" '
        $0 ~ "^## " v "( |$)" { on = 1; next }
        on && /^## /          { exit }
        on                    { print }
    ' RELEASE_NOTES.md | sed -e '/./,$!d' > "$BUILD_DIR/notes.md"

    log "release notes"
    cat "$BUILD_DIR/notes.md"

    if [ "$ASSUME_YES" -eq 0 ]; then
        printf '\n+++ push `%s` + tag `%s` to origin and publish GitHub release? [y/N] ' "$BRANCH" "$VERSION"
        read -r ANSWER
        case "$ANSWER" in
            y|Y|yes) ;;
            *) log "not pushed. commit + tag are local"
               echo "    publish : dist/release.sh $VERSION --publish-only"
               echo "    undo    : git tag -d $VERSION && git reset --hard HEAD~1"
               exit 0 ;;
        esac
    fi

    log "pushing"
    git push origin "$BRANCH" "$VERSION"

    log "creating GitHub release"
    gh release create "$VERSION" teilchen.zip teilchen.txt \
        --title "$RELEASE_DATE" --notes-file "$BUILD_DIR/notes.md" --latest

    log "done: $(gh release view "$VERSION" --json url -q .url)"
}

if [ "$PUBLISH_ONLY" -eq 1 ]; then
    publish
    exit 0
fi

# ---------------------------------------------------------------------------------------------------------------------

log "setting version"

perl -pi -e "s/^version\s*=.*/version = $LIB_VERSION/; s/^prettyVersion\s*=.*/prettyVersion = $PRETTY_VERSION/" \
    lib/library.properties
# `teilchen.txt` is the copy of `library.properties` the processing contribution manager reads
cp lib/library.properties teilchen.txt
# `## v0.5` or `## v0.5 (unreleased)` → `## v0.5 (20261104)` ( not for test builds ). keeps the line break
if [ "$DO_GIT" -eq 1 ]; then
    perl -pi -e "s/^## \Q$VERSION\E([ \t].*)?\$/## $VERSION ($RELEASE_DATE)/" RELEASE_NOTES.md
fi
grep -E "^(version|prettyVersion)" lib/library.properties

# ---------------------------------------------------------------------------------------------------------------------

log "compiling ( $(basename "$CORE_JAR"), java $JAVAC_MAJOR → release 17 )"

CLASSES="$BUILD_DIR/classes"
mkdir -p "$CLASSES"
find src -name '*.java' > "$BUILD_DIR/sources.txt"
"$JAVAC" --release 17 -g -encoding UTF-8 -nowarn -Xlint:none -d "$CLASSES" -cp "$CLASSPATH_BUILD" \
    @"$BUILD_DIR/sources.txt" > "$BUILD_DIR/javac.log" 2>&1 || { cat "$BUILD_DIR/javac.log"; fail "compilation failed"; }
# non-java resources ( e.g READMEs ) are packed into the jar as well
(cd src && find . -type f ! -name '*.java' ! -name '.DS_Store' ! -name '*.iml' -exec sh -c \
    'mkdir -p "$0/$(dirname "$1")" && cp "$1" "$0/$1"' "$CLASSES" {} \;)
echo "$(find "$CLASSES" -name '*.class' | wc -l | tr -d ' ') classes"

# ---------------------------------------------------------------------------------------------------------------------

if [ "$DO_TESTS" -eq 1 ]; then
    log "running tests"
    for TEST in teilchen.test.TestIntegrators teilchen.test.TestColliders teilchen.test.TestGeometry; do
        "$JAVA" -Djava.awt.headless=true -cp "$CLASSES:$CLASSPATH_BUILD" "$TEST" > "$BUILD_DIR/test.log" 2>&1 ||
            { cat "$BUILD_DIR/test.log"; fail "$TEST failed"; }
        echo "$TEST: $(tail -n 1 "$BUILD_DIR/test.log")"
    done
fi

# ---------------------------------------------------------------------------------------------------------------------

log "packing lib/teilchen.jar"

rm -f lib/teilchen.jar
"$JAR" --create --file lib/teilchen.jar -C "$CLASSES" .
ls -lh lib/teilchen.jar | awk '{print $5, $9}'

# ---------------------------------------------------------------------------------------------------------------------

log "generating reference ( javadoc )"

PACKAGES=$(tr '\n' ' ' < docs/element-list)
rm -rf docs
"$JAVADOC" -quiet -Xdoclint:none -encoding UTF-8 -charset UTF-8 -docencoding UTF-8 --release 17 \
    -sourcepath src -cp "$CLASSPATH_BUILD" -d docs -windowtitle "teilchen $PRETTY_VERSION" $PACKAGES \
    > "$BUILD_DIR/javadoc.log" 2>&1 || { tail -n 40 "$BUILD_DIR/javadoc.log"; fail "javadoc failed"; }
grep -E "^[0-9]+ (error|warning)" "$BUILD_DIR/javadoc.log" || true

# ---------------------------------------------------------------------------------------------------------------------

log "building processing library + teilchen.zip ( dist/build_dist.sh )"

(cd dist && bash ./build_dist.sh > "$BUILD_DIR/dist.log" 2>&1) || { cat "$BUILD_DIR/dist.log"; fail "build_dist.sh failed"; }
[ -f teilchen.zip ] || fail "teilchen.zip was not created"
ls -lh teilchen.zip | awk '{print $5, $9}'

if [ "$DO_GIT" -eq 0 ]; then
    log "done ( build only ). nothing committed"
    exit 0
fi

# ---------------------------------------------------------------------------------------------------------------------

log "committing + tagging $VERSION"

git add -A lib docs processing-library teilchen.zip teilchen.txt RELEASE_NOTES.md
git commit --quiet -m "release $VERSION"
git tag -a "$VERSION" -m "teilchen $VERSION ($RELEASE_DATE)"
git log --oneline -1

if [ "$DO_PUSH" -eq 0 ]; then
    log "done. commit + tag are local. publish later with:"
    echo "    dist/release.sh $VERSION --publish-only"
    exit 0
fi

# ---------------------------------------------------------------------------------------------------------------------

publish
