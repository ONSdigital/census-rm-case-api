package uk.gov.ons.census.caseapisvc;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("uk.gov.ons.census.common.model.entity")
@OpenAPIDefinition(
    info =
        @Info(
            title = "Case API Service",
            description = "Service for managing case data",
            version = "v1"),
    servers = {@Server(url = "http://localhost:8161")})
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
