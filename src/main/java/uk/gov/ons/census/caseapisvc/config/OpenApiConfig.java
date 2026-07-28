package uk.gov.ons.census.caseapisvc.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Value("${app.version:1.0.0-SNAPSHOT}")
  private String appVersion;

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Response Management Case API")
                .version(appVersion)
                .description(
                    "RESTful API service providing case retrieval operations, address lookups,"
                        + " and case event details for Response Management integration consumers.")
                .contact(
                    new Contact().name("ONS Response Management Team").email("support@ons.gov.uk")))
        .servers(
            List.of(
                new Server().url("/").description("Default Ingress Relative Path"),
                new Server()
                    .url("https://case-api.rm.census.gov.uk")
                    .description("GCP Production Cluster Ingress")));
  }
}
