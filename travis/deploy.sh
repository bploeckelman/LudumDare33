#!/usr/bin/env bash

project="$(basename $(pwd))"

mkdir deploy
cp -r html/build/dist/* deploy/
cp desktop/build/libs/desktop-*.jar "deploy/${project}.jar"

cd deploy
# Tarball
# tar -czf "../${project}.tar.gz" ./

# Zip
zip -r "../${project}.zip" ./
cd ..

# TODO: Upload the build somewhere; bintray maybe?
# curl -T "${project}.zip" -uuser:<API_KEY> https://api.bintray.com/content/user/generic/<YOUR_COOL_PACKAGE_NAME>/<VERSION_NAME>/<FILE_TARGET_PATH>
