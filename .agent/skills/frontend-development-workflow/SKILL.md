---
name: Frontend Development Workflow
description: standard commands and processes for frontend development
---

# Frontend Development Workflow

## Standard Commands

Run these commands from `src/main/frontend`:

- **Start Development Server**: 
  ```bash
  npm run dev
  ```
  This first runs `orval` to generate the latest API client, then starts `vite`.

- **Type Check & Build**:
  ```bash
  npm run build
  ```
  Runs `tsc` (TypeScript Compiler) to check types and `vite build` to produce production assets.

- **Linting**:
  ```bash
  npm run lint
  ```
  Uses ESLint to analyze code for potential errors and style violations.

- **Testing**:
  ```bash
  npm run test
  ```
  Runs unit tests using Vitest.

## Code Standards
- **Functional Components**: Use React Functional Components with Hooks.
- **Strict TypeScript**: Avoid `any`. Use the types generated in `src/api/schemas` for API data.
- **Imports**: Use the `@/` alias to import from `src/`.
  - Example: `import { Button } from "@/components/ui/button"`

## Adding Dependencies
Use `npm install <package>` for runtime dependencies and `npm install -D <package>` for dev dependencies.
