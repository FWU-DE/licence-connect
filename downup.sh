#/bin/bash -euo pipefail

LC_IMAGE=lc_core:0.0.1-SNAPSHOT docker compose -f ./docker/docker-compose.yaml down
./mvnw clean spring-boot:build-image -DskipTests
LC_IMAGE=lc_core:0.0.1-SNAPSHOT docker compose -f ./docker/docker-compose.yaml up --build -d --wait
echo -e $(curl -X GET http://localhost:8080/v1/healthcheck)

