---
name: Frontend Component Structure
description: guidelines for organizing and creating components
---

# Frontend Component Structure

The project follows a feature-based architecture combined with specific directories for shared UI elements.

## Directory Overview

- **`src/components/ui`**:
  - Contains **Shadcn/UI** components (Button, Input, Card, etc.).
  - These are "dumb" components: highly reusable, style-focused, and logic-free.
  - Do not modify these unless you are customizing the design system itself.

- **`src/features`**:
  - Contains domain-specific logic and components.
  - Structure: `src/features/<feature-name>/`.
  - Example: `src/features/security`, `src/features/clusters`.
  - A feature folder should contain components, hooks, and types specific to that business domain.

- **`src/pages`**:
  - Top-level page components corresponding to routes.
  - Pages should primarily compose features and layout components.
  - Located in `src/pages/`.

- **`src/layouts`**:
  - Component wrappers that define the page structure (headers, sidebars, etc.).
  - Example: `SetupLayout`.

## Component Guidelines

1. **Colocation**: Keep related logic close to where it's used. If a component is only used in one feature, put it in that feature's folder.
2. **Reusability**: If a component is generic (e.g., a specific LoadingSpinner), put it in `src/components`.
3. **Naming**: Use PascalCase for component files (e.g., `ClusterList.tsx`).
4. **Exports**: Prefer named exports over default exports for components.

## Styling
- Use **Tailwind CSS** classes directly in JSX.
- Use `cn()` utility (from `@/lib/utils`) to merge conditional classes.
  ```tsx
  <div className={cn("bg-red-500", isActive && "bg-green-500")}>...</div>
  ```
