---
name: Workflow Release
description: Understands the release process, including tag verification, Docker build/push, and documentation publishing.
---

# Release Workflow

The release workflow builds and publishes the application artifacts (Docker image, Documentation). It is defined in `.github/workflows/build-and-push.yml`.

## Workflows

### 1. Build and Push (`build-and-push.yml`)
- **Triggers**: 
  - Push of **tags** (`*`) - Only tags trigger this workflow!
- **Jobs**:
  - `build-and-push`: Orchestrates the build and publish process.

## Process Details

The release process is encapsulated in the `.github/actions/build-interfero` composite action.

### 1. Version Verification (`verify-tag`)
- **Action**: `.github/actions/verify-tag`
- **Logic**: 
  - Extracts the git tag name.
  - Extracts the version from `pom.xml`.
  - **Fails** if the tag does not match the POM version.
- **Production Check**: 
  - If tag matches `X.Y.Z` (Semantic Versioning), it is considered a **PROD RELEASE**.
  - Otherwise, it is a **PRE-RELEASE**.

### 2. Build Artifacts
- **Command**: `mvn clean package --batch-mode -Pproduction -DskipTests`
- **Profile**: `production` is active.
- **Tests**: Tests are SKIPPED during the release build (they run in separate steps before packaging).

### 3. Documentation
- **Artifact**: `target/spring-modulith-docs`
- **Action**: Saved as a GitHub Artifact named `documentation`.

### 4. Docker Publish
- **Image**: `dennisschaefer/interfero`
- **Registry**: Docker Hub
- **Tagging Strategy**:
  - **Pre-Release**: Pushes `dennisschaefer/interfero:<TAG>`
  - **Prod-Release**: Pushes `dennisschaefer/interfero:<TAG>` AND `dennisschaefer/interfero:latest`

## Agent Best Practices
- **Releases**: To trigger a release, you simply need to push a git tag.
- **Version Mismatches**: Ensure `pom.xml` version is updated BEFORE pushing the tag.
- **Secrets**: This workflow requires `DOCKER_HUB_USERNAME` and `DOCKER_HUB_TOKEN` secrets.
