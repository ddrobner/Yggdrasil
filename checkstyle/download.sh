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
CHECKSTYLE_MAVEN_URL="http://downloads.sourceforge.net/project/checkstyle/checkstyle/6.11.2/checkstyle-6.11.2-all.jar?r=http%3A%2F%2Fsourceforge.net%2Fprojects%2Fcheckstyle%2Ffiles%2Fcheckstyle%2F6.11.2%2F&ts=1444367565&use_mirror=iweb"

echo "Will download $CHECKSTYLE_MAVEN_URL to $(pwd)/$FILENAME. Continue?"

select yn in "Yes" "No"; do
    case $yn in
        Yes) wget --user-agent="$USER_AGENT" --output-document="$FILENAME" "$CHECKSTYLE_MAVEN_URL"
            break
            ;;
        No) exit 1;;
    esac
done
