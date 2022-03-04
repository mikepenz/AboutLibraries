#!/bin/bash

rm -rf "docs/"
mkdir -p "docs/plugin"
./gradlew :plugin-build:plugin:dokkaHtml --no-parallel
cp -r "plugin-build/plugin/build/dokka/html/" "docs/plugin"
./gradlew dokkaHtmlMultiModule --no-parallel
cp -r "build/dokka/htmlMultiModule/" "docs"