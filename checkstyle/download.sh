#!/bin/sh

DEFAULT_VERSION="6.11.2"

if [ $# -gt 1 ]; then
    echo "Expected no arguments (download default) or one argument (specify version)"
    exit 1
fi

if [ $# -eq 0 ]; then
    VERSION="$DEFAULT_VERSION"
else
    VERSION="$1"
fi

FILENAME="checkstyle-$VERSION-all.jar"
USER_AGENT="Mozilla/5.0 (X11; Linux i586; rv:31.0) Gecko/20100101 Firefox/31.0"
DOWNLOAD_URL="http://sourceforge.net/projects/checkstyle/files/checkstyle/$VERSION/checkstyle-$VERSION-all.jar/download"

echo "Will download $DOWNLOAD_URL to $(pwd)/$FILENAME. Continue?"

select yn in "Yes" "No"; do
    case $yn in
        Yes) curl -L "$DOWNLOAD_URL" -o "$FILENAME"
            break
            ;;
        No) exit 1;;
    esac
done
