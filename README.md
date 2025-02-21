# Licence Connect Core Application
## Introduction and Goals

Licence Connect enables schools, school boards, and federal German states to manage educational resources by buying them, assigning them, and making them available to users at schools.

Licence Connect Core provides a way of accessing licences in a unified way.

## Table of Contents

- [Setup](#setup)
  - [Prerequisites](#prerequisites)
  - [Environment Variables](#environment-variables)
  - [Building the Project](#building-the-project)
  - [Running the Application](#running-the-application)
  - [Running Tests](#running-tests)
  - [Docker Deployment](#docker-deployment)
- [License](#license)

## Setup

### Prerequisites

- Java 21
- Maven 3.x
- Docker (optional, for containerized deployment)

### Environment Variables

The application requires several environment variables to be set for proper configuration. These variables are used to populate the `application.properties` file.

- `BILO_V1_PASSWORD`: Password for the Bilo V1 admin user with which licenceconnect authenticates its requests to BildungsLogin V1.
- `BILO_V2_CLIENT_ID`: Client ID with which licenceconnect authenticates its requests to BildungsLogin V2.
- `BILO_V2_CLIENT_SECRET`: Client secret with which licenceconnect authenticates its requests to BildungsLogin V2.
- `VIDIS_API_KEY`: API Key with which VIDIS can authenticate its requests to licenceconnect.

You need to have all the Environment Variables set before running any of the following commands which can be done either by setting them in the environment or by passing them as arguments to the maven command as shown below:

```sh
BILO_V1_PASSWORD=<admin password> VIDIS_API_KEY=<vidis key> BILO_V2_CLIENT_ID=<bilo client id> BILO_V2_CLIENT_SECRET=<bilo secret>  <COMMAND>
```

### Building the Project

To build the project, run the following command:

```sh
mvn clean install
```

### Active Profiles

Several profiles allow for different configurations of the application. The following profiles are available:
- `auto-start-mocks`: Automatically starts the dockerized mock-servers
- `local`: Configures the application to query against the local mock servers
If the profile is set to `auto-start-mocks` when running the application (e.g. `mvn test -Dspring.profiles.active=auto-start-mocks`, the mock servers will be started automatically.


### Running the Application

To run the application, use the following command:

```sh
mvn spring-boot:run
```

On local development, the swagger UI can be accessed at `http://localhost:8080/swagger-ui/index.html`

### Running Tests

To run the tests, use the following command:

```sh
mvn test
```

### Docker

To build the Docker image locally, use the following command:

```sh
./mvnw spring-boot:build-image -DskipTests
```

To run the Docker image locally, use the following command:
```sh
docker run -e BILO_V1_PASSWORD=<password> -e BILO_V2_CLIENT_ID=<client_id> -e BILO_V2_CLIENT_SECRET=<client_secret> -e VIDIS_API_KEY=<api_key> -p 8080:8080 lc-core:latest
```

### Deployment

The app is deployed for every push to the `main` branch.
It is deployed onto the `licence-connect-api.fwu.nhnbg` VM, which can be reached via the VPN provided by Netz-Haut GmbH for the FWU. The user used to deploy and run the app is `docker`, whose home directory on the VM is `/var/docker`. The app resides in `/var/docker/apps/licenceconnect`

Externally, the app can be reached via `https://api.licenceconnect.schule/`, with a publicly available swagger UI on `https://api.licenceconnect.schule/swagger`.

The exact deployment process can be seen inside `.gitlab-ci.yml`, but essentially, the pipeline
- builds a new docker image
- copies the `docker/docker-compose.yaml` file and the `docker/nginx` directory to the VM
- restarts the docker services

On the server, instead of providing the environment variables directly to the docker compose command, there is an `.env` file located in the app folder in which all required environment variables must be set.

## Mock Licence Servers

To allow easy local testing and testing without being dependent on the availability of external systems, we provide a mock for every licence server we support in `src/mock-licence-servers`.
Currently, this includes:
- Arix
- Bildungslogin V1
- Bildungslogin V2

All licence servers can be individually started in a docker container. All licence servers can be started simultaneously by running `docker-compose up` in `src/mock-licence-servers`.

### Arix

There are two versions of Arix mock servers:
- `arix-accepting` to allow testing the connection to an Arix server that has whitelisted lc core.
- `arix-rejecting` to allow testing the connection to an Arix server that has not whitelisted lc core.

Please be aware that neither is a complete Arix server but rather only provides the functionality needed for the tests currently implemented.

## License

This project is licensed under the Apache License 2.0 - see the `LICENSE` file for details.
