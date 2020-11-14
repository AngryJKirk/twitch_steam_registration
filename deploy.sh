#!/bin/bash

./gradlew clean build -x test &&
docker build . -f Dockerfile -t wprot-rp &&
docker kill $(docker ps -q --filter publish=8080 --format="{{.ID}}")
docker run -d --restart always -p 127.0.0.1:8080:8080 wprot-rp:latest