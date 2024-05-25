#!/bin/bash
./gradlew build
# Run the Gradle tasks and save the output
app_name=$(./gradlew printName --quiet)
echo "App name: $app_name"
app_version=$(./gradlew printVersion --quiet)
echo "App version: $app_version"

# Use the output in the Docker build command

echo "Building Docker image with the following command:"
echo "docker build --build-arg appName=$app_name --build-arg version=$app_version -t $app_name:$app_version ."
docker build --build-arg appName=$app_name --build-arg version=$app_version -t $app_name:$app_version .