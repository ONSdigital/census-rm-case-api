package uk.gov.ons.census.caseapisvc.endpoint;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DocumentationGeneratorIT {
  @LocalServerPort private int port;

  /*
  Clearly this is NOT a test. This is a way of using Spring Boot's integration testing plugin utils
  to update our machine-generated documentation without having to include any of it in the
  production build.

  The alternative would have been to use springdoc-openapi-maven-plugin but this won't work because
  our app won't start up without a database etc, and the app has to be running for the plugin to
  work. Also, it would have meant bundling all the openAPI JARs into our prod build and exposing
  endpoints which could be a security risk.
   */
  @Test
  public void generateDocs() throws IOException, InterruptedException {
    RestTemplate restTemplate = new RestTemplate();
    String url = "http://localhost:" + port + "/v3/api-docs";
    String apiSpec = restTemplate.getForObject(url, String.class);
    assertThat(apiSpec).isNotBlank();

    try (FileOutputStream fos = new FileOutputStream("api-docs/openapi.json")) {
      fos.write(apiSpec.getBytes());
    }

    int mdExitStatus =
        runCommand(
            "npx",
            "--yes",
            "widdershins@4.0.1",
            "api-docs/openapi.json",
            "-o",
            "api-docs/openapi.md");

    int htmlExitStatus =
        runCommand(
            "npx",
            "--yes",
            "@redocly/cli",
            "build-docs",
            "api-docs/openapi.json",
            "-o",
            "api-docs/openapi.html");

    assertThat(mdExitStatus).isZero();
    assertThat(htmlExitStatus).isZero();
  }

  private int runCommand(String... command) throws IOException, InterruptedException {
    Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
    String output;
    try (var processOutput = process.getInputStream()) {
      output = new String(processOutput.readAllBytes(), StandardCharsets.UTF_8);
    }

    int exitStatus = process.waitFor();
    if (exitStatus != 0) {
      throw new IOException(
          "Command failed with exit code "
              + exitStatus
              + ": "
              + String.join(" ", command)
              + System.lineSeparator()
              + output);
    }
    return exitStatus;
  }
}
