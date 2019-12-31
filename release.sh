#!/bin/bash

./gradlew clean build

if [ "$1" = "release" ];
then
    ./gradlew library-core:bintrayUpload -Plibrary_core_only
    ./gradlew library:bintrayUpload -x test -x lint -Plibrary_only
    ./gradlew library-definitions:bintrayUpload -x test -x lint -Plibrary_definitions_only
else
    //TODO
fi
