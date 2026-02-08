---
name: Workflow CI Frontend
description: Understands the frontend CI/CD pipeline, including Node.js dependencies and tests.
---

# Frontend CI Workflow

The frontend CI pipeline validates the Node.js application. It is defined in `.github/workflows/frontend-tests.yml`.

## Workflows

### 1. Frontend Tests (`frontend-tests.yml`)
- **Triggers**: 
  - Push to any branch (`*`)
  - Pull Requests
  - Manual Dispatch (`workflow_dispatch`)
  - Reusable Call (`workflow_call`)
- **Jobs**:
  - `frontend-tests`: Runs all frontend tests.

## Local Execution Guide

To simulate the CI steps locally, use the following commands from `src/main/frontend`.

### Prerequisites in CI
- **Node.js**: Version 24 (using `actions/setup-node@v6`).
- **NPM Cache**: `npm` cache is managed via `cache: 'npm'`.

### Installing Dependencies
Dependencies are installed using `npm ci`. This ensures deterministic builds based on `package-lock.json`.

**Command:**
```bash
cd src/main/frontend
npm ci
```

### Running Tests
The CI uses the `.github/actions/frontend-tests` composite action.

**Command:**
```bash
cd src/main/frontend
npm run test
```

## Agent Best Practices
- **Dependency Issues**: If `npm ci` fails due to lockfile discrepancies, verify that `package-lock.json` is updated and committed.
- **Node Version**: Ensure you are using a compatible Node.js version (Recommended: Node 24).
- **Working Directory**: ALWAYS execute frontend commands from `src/main/frontend` unless otherwise specified.
