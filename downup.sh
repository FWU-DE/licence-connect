#/bin/bash -euo pipefail

# Check if the directory ./docker/db belongs to GID=1000 and UID=1002 which are the user and group of the docker user in the container.
DIR=${DB_FILE_PATH:-"./docker/db"}
EXPECTED_GID=1000
EXPECTED_UID=1002

if [[ "$OSTYPE" != "darwin"* ]]
 then
    # Linux
    DIR_GID=$(stat -c '%g' "$DIR")
    DIR_UID=$(stat -c '%u' "$DIR")

  if [[ "$DIR_GID" -ne "$EXPECTED_GID" || "$DIR_UID" -ne "$EXPECTED_UID" ]]; then
      echo "ERROR: Directory $DIR does not belong to GID=$EXPECTED_GID and UID=$EXPECTED_UID."
      echo "Actual GID=$DIR_GID and UID=$DIR_UID. Make sure to run chown -R 1000:1002 ./docker/db"
      exit 1
  fi
fi

LC_IMAGE=lc_core:0.0.1-SNAPSHOT docker compose -f ./docker/docker-compose.yaml down
./mvnw clean spring-boot:build-image -DskipTests
LC_IMAGE=lc_core:0.0.1-SNAPSHOT docker compose -f ./docker/docker-compose.yaml up --build -d --wait
echo -e $(curl -X GET http://localhost:8080/v1/healthcheck)

