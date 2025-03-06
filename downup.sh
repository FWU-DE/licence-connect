#/bin/bash -euo pipefail

LC_IMAGE=lc_core:0.0.1-SNAPSHOT docker compose -f ./docker/docker-compose.yaml down
docker build -t "mana" ./docker
./mvnw clean spring-boot:build-image -DskipTests
LC_IMAGE=lc_core:0.0.1-SNAPSHOT docker compose -f ./docker/docker-compose.yaml up --build -d --wait
docker exec -it licence-connect /bin/bash -c 'ls -halt /workspace/'
