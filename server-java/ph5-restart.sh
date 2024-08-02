docker container inspect ph5 &>/dev/null && docker container stop ph5 && docker container rm ph5
docker image inspect zebrajaeger/ph5:arm64-0.0.1-SNAPSHOT &>/dev/null && docker image rm  $(docker images -q 'zebrajaeger/ph5' | uniq)
docker image load -i  ph5-arm64-0.0.1-SNAPSHOT.tar
docker run -d -v ~/ph5:/root/.config/panohead --publish 8080:8080 --publish 8443:8443 --name ph5 zebrajaeger/ph5:arm64-0.0.1-SNAPSHOT