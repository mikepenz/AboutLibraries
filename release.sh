#!/bin/bash

./gradlew clean build

if [ "$1" = "release" ];
then
    ./gradlew library-core:publishReleasePublicationToSonatypeRepository -Plibrary_core_only
    ./gradlew library:publishReleasePublicationToSonatypeRepository -x test -x lint -Plibrary_only
    ./gradlew library-definitions:publishReleasePublicationToSonatypeRepository -x test -x lint -Plibrary_definitions_only
else
    //TODO
fi
