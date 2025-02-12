# Licence Connect Core Application
## Introduction and Goals

Licence Connect enables schools, school boards, and federal German states to manage educational resources by buying them, assigning them, and making them available to users at schools.

Licence Connect Core provides a way of accessing licenses in a unified way.

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

The application requires several environment variables to be set for proper configuration. These variables are used to
populate the `application.properties` file.

- `BILO_V1_PASSWORD`: Password for the Bilo V1 admin user with which licenceconnect authenticates its requests to BildungsLogin V1.
- `BILO_V2_CLIENT_ID`: Client ID with which licenceconnect authenticates its requests to BildungsLogin V2.
- `BILO_V2_CLIENT_SECRET`: Client secret with which licenceconnect authenticates its requests to BildungsLogin V2.
- `VIDIS_API_KEY`: API Key with which VIDIS can authenticate its requests to licenceconnect.

You need to have all the Environment Variables set before running any of the following commands which can be done either
by setting them in the environment or by passing them as arguments to the maven command as shown below:

```sh
BILO_V1_PASSWORD=<admin password> VIDIS_API_KEY=<vidis key> BILO_V2_CLIENT_ID=<bilo client id> BILO_V2_CLIENT_SECRET=<bilo secret>  <COMMAND>
```

### Building the Project

To build the project, run the following command:

```sh
mvn clean install
```

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

### Docker Deployment

To build the Docker image, use the following command:

```sh
./mvnw spring-boot:build-image -DskipTests
```

To run the Docker image locally, use the following command:
```sh
docker run -e BILO_V1_PASSWORD=<password> -e BILO_V2_CLIENT_ID=<client_id> -e BILO_V2_CLIENT_SECRET=<client_secret> -e VIDIS_API_KEY=<api_key> -p 8080:8080 lc-core:latest
```

On the server, instead of providing the environment variables directly to the command, copy the docker/.env.example to ./.env on the server and fill in the credentials. The credentials can be found in the FLC vault.


## License

This project is licensed under the Apache License 2.0 - see the `LICENSE` file for details.

