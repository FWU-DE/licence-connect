package com.fwu.lc_core;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.nio.file.FileSystems;

@TestConfiguration(proxyBeanMethods = false)
public class DockerComposeManager {

//    @Bean
//    @ServiceConnection
//    @ConditionalOnProperty(name = "spring.docker.compose.skip.in-tests", havingValue = "false", matchIfMissing = true)
//    public DockerComposeContainer<?> dockerComposeContainer() {
//        var composeFilePath = FileSystems.getDefault().getPath("src", "mock-licence-server", "docker-compose.yml").toAbsolutePath();
//
//        // Ensure the path to the compose file is correct
//        File composeFile = composeFilePath.toFile();
//        if (!composeFile.exists()) {
//            throw new IllegalStateException("Compose file not found: " + composeFilePath);
//        }
//
//        // Ensure the service name and port match those in your compose file
//        return new DockerComposeContainer<>(composeFile)
//                .withExposedService(1234, Wait.forListeningPort());
//    }
}
