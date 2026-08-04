# Case API - OpenAPI Documentation Guide

## 📋 Overview

The Case API generates OpenAPI specifications automatically from the codebase
using Spring Boot and `springdoc-openapi`.

Specifications are generated in CI/CD on every push to `main`.

The source of truth is the version-controlled `api-docs/openapi.json` file in
this repository, generated from the current endpoint/model code by
`DocumentationGeneratorIT`.

## 🎯 For Integration Teams

### How to Access the Latest API Specification

1. **Repository Source of Truth**

   - Use committed `api-docs/openapi.json` as the canonical contract
   - Review API contract changes via normal pull request diffs

2. **GitHub Actions Artifacts**
   - Navigate to the workflow run:
     `Census31 RM Case API` → `Case API OpenAPI Document Generation CI`
   - Download the artifact `rm-case-api-openapi-specs.zip`
   - Unzip to get generated docs:
      - `openapi.md` - Markdown documentation (derived from `openapi.json`)
      - `openapi.html` - Interactive ReDoc browser (derived from `openapi.json`)
      - `openapi.json` - Included for convenience and traceability

3. **Available Formats**

   | Format | File | Use case |
   | ------ | ------------ | ------------------------- |
   | JSON | `openapi.json` | Canonical contract, gateway config, codegen, validation |
   | Markdown | `openapi.md` | Wiki and README docs |
   | HTML | `openapi.html` | Interactive browser view |

### When Specs Are Updated

- **On every commit to `main` branch** - `openapi.json` is regenerated and checked
  against the committed copy; derived docs are published as artifacts
- **On every pull request** - the same contract check runs and fails if
  `api-docs/openapi.json` is stale
- **Retention:** Artifacts kept for 90 days

The `api-docs/` directory is created during the integration test run, so
developers do not need to create it manually.

## 🔧 Technical Details

### Generation Process

```text
Code Changes → Commit to main → GitHub Actions CI
    ↓
1. Build & Run Tests (including DocumentationGeneratorIT)
2. Start Spring Boot with test database
3. Query `/v3/api-docs` endpoint (Springdoc)
4. Write deterministic JSON output (`springdoc.writer-with-order-by-keys=true`)
5. Generate Markdown (Widdershins)
6. Generate Interactive HTML (ReDoc)
7. Lint `openapi.json` structurally (`@redocly/cli lint`)
8. Fail CI if committed `api-docs/openapi.json` differs from generated output
9. Upload artifacts to GitHub Actions
```

### What's Included in the Spec

✅ **Included automatically:**

- All REST endpoints with HTTP methods
- Request/response schemas
- Query parameters and path variables
- HTTP status codes
- Basic descriptions from `@OpenAPIDefinition` and `@Operation` annotations

⏳ **Planned enhancements:**

- Detailed parameter descriptions
- Example request/response payloads
- Error codes and validation rules
- Authentication/authorization requirements

---

## Developer Notes

### For Case API Developers

To improve the generated documentation:

1. **Basic annotations (Minimum)**

   ```java
   @Operation(summary = "Get case by ID")
   @ApiResponses({
     @ApiResponse(responseCode = "200", description = "Case found"),
     @ApiResponse(responseCode = "404", description = "Case not found")
   })
   public ResponseEntity<CaseContainerDTO> getCaseById(
       @PathVariable UUID caseId) {
     // ...
   }
   ```

2. **Rich annotations (Optional - Phase 2)**

   ```java
   @Operation(
     summary = "Get case by ID",
     description = "Retrieves a census case record by its unique identifier"
   )
   @Parameter(
     name = "caseId",
     description = "The UUID of the case to retrieve",
     example = "550e8400-e29b-41d4-a716-446655440000"
   )
   public ResponseEntity<CaseContainerDTO> getCaseById(
       @PathVariable UUID caseId) {
     // ...
   }
   ```

### How to Test Locally

```bash
# Generate docs locally
make build

# Docs are generated in:
# - api-docs/openapi.json
# - api-docs/openapi.md
# - api-docs/openapi.html

# Open the interactive spec in your browser
open api-docs/openapi.html
```

### Troubleshooting

**Spec not updating?**

- Run `mvn clean` to remove cached artifacts
- Ensure you're building with integration tests: `make build` or `mvn verify`
- Commit the regenerated `api-docs/openapi.json` in the same pull request as
  your endpoint/model change

**JSON changed in git after API-related code changes?**

- This is expected when the API contract changes
- CI enforces that committed `api-docs/openapi.json` matches fresh generation

---