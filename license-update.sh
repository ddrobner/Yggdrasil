#!/bin/bash

if [ $# -ne 1 ]; then
    echo 'Enter a year!'
    exit 1
fi

year=$1

if [[ ! "$year" =~ [0-9]+ ]]; then
    echo 'Year must be numeric!'
    exit 1
fi

find src -type f -name "*.java" -print0 | xargs -0 sed -i -e "s|Copyright (c) \([[:digit:]]\+\), FRC3161|Copyright (c) \1-$year, FRC3161|"
find src -type f -name "*.java" -print0 | xargs -0 sed -i -e "s|Copyright (c) \([[:digit:]]\+\)-\([[:digit:]]\+\), FRC3161|Copyright (c) \1-$year, FRC3161|"
