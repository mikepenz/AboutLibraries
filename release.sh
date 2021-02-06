#!/bin/bash

./gradlew clean build

if [ "$1" = "release" ];
then
    ./gradlew aboutlibraries-core:publishReleasePublicationToSonatypeRepository -Plibrary_core_only
    ./gradlew aboutlibraries:publishReleasePublicationToSonatypeRepository -x test -x lint -Plibrary_only
    ./gradlew aboutlibraries-definitions:publishReleasePublicationToSonatypeRepository -x test -x lint -Plibrary_definitions_only
else
    //TODO
fi
