# Licence Connect Core Application

## Table of Contents

- [Prerequisites](#prerequisites)
- [Building the Project](#building-the-project)
- [Running the Application](#running-the-application)
- [Environment Variables](#environment-variables)
- [Running Tests](#running-tests)
- [Docker Deployment](#docker-deployment)
- [License](#license)

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

To build and run the Docker image, use the following commands:

```sh
./mvnw spring-boot:build-image -DskipTests
docker run -e BILO_V1_PASSWORD=<password> -e BILO_V2_CLIENT_ID=<client_id> -e BILO_V2_CLIENT_SECRET=<client_secret> -e VIDIS_API_KEY=<api_key> -p 8080:8080 lc-core:latest
```

## License

This project is licensed under the Apache License 2.0 - see the `LICENSE` file for details.

## Mock License Servers
To allow easy local testing and testing without being dependent on the availability of external systems, we provide a mock for every licence server we support in `src/mock-licence-servers`.
Currently, this includes:
- Arix 

All licence servers can be individually started in a docker container. All licence servers can be started simultaneously by running `docker-compose up` in `src/mock-licence-servers`.

If the profile is set to `auto-start-docker` when running the application (e.g. `mvn test -Dspring.profiles.active=auto-start-mocks`, the mock servers will be started automatically.


### Arix
There are two versions of Arix mock servers: 
- `arix-accepting` to allow testing the connection to an Arix server that has whitelisted lc core. 
- `arix-rejecting` to allow testing the connection to an Arix server that has not whitelisted lc core.

Please be aware that neither is a complete Arix server but rather only provides the functionality needed for the tests currently implemented.