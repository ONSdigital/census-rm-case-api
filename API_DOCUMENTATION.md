# Case API - OpenAPI Documentation Guide

## 📋 Overview

The Case API generates OpenAPI specifications automatically from the codebase
using Spring Boot and `springdoc-openapi`.

Specifications are generated in CI/CD on every push to `main`. The latest
successful main-branch artifact is the source of truth for integration teams.

## 🎯 For Integration Teams

### How to Access the Latest API Specification

1. **GitHub Actions Artifacts**
   - Navigate to the workflow run:
     `Census31 RM Case API` → `Case API OpenAPI Document Generation CI`
   - Download the artifact `rm-case-api-openapi-specs.zip`
   - Unzip to get three formats:
     - `openapi.json` - Machine-readable API contract (for tooling)
     - `openapi.md` - Markdown documentation
     - `openapi.html` - Interactive ReDoc browser (open in browser)

2. **Available Formats**

   | Format | File | Use case |
   | ------ | ------------ | ------------------------- |
   | JSON | `openapi.json` | Gateway config, codegen, validation |
   | Markdown | `openapi.md` | Wiki and README docs |
   | HTML | `openapi.html` | Interactive browser view |

### When Specs Are Updated

- **On every commit to `main` branch** - Specs are regenerated and published
- **On every pull request** - Specs are validated but not published
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
4. Normalize JSON output (alphabetical keys, clean formatting)
5. Generate Markdown (Widdershins)
6. Generate Interactive HTML (ReDoc)
7. Upload artifacts to GitHub Actions
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

**JSON keeps changing in git?**

- This is intentional - we normalize JSON to ensure clean diffs
- Only actual API changes will show in diffs

---