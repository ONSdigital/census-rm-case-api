package uk.gov.ons.census.caseapisvc;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
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
            description =
                "RESTful API for managing census case data. Provides endpoints for querying and retrieving case information by various identifiers (UUID, reference, UPRN, postcode, QID). "
                    + "Returns comprehensive case details including address information, case status, and associated events.",
            version = "v1",
            license =
                @License(name = "Office for National Statistics", url = "https://www.ons.gov.uk")),
    servers = {@Server(url = "${openapi.server-url}", description = "Case API Service")})
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
