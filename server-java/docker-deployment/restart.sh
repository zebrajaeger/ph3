#!/bin/sh

echo "##### Stop stack #####"
docker compose stop
echo "##### Remove Container #####"
docker container inspect ph5 > /dev/null 2>&1 && echo "exists: remove container" &&  docker container rm ph5
echo "##### Remove image #####"
docker image inspect zebrajaeger/ph5:arm64-0.0.1-SNAPSHOT > /dev/null 2>&1 && echo "exists: remove image" && docker image rm zebrajaeger/ph5:arm64-0.0.1-SNAPSHOT
echo "#####Load image #####"
docker image load -i  ph5-arm64-0.0.1-SNAPSHOT.tar
echo "##### Start stack #####"
docker compose up -d
