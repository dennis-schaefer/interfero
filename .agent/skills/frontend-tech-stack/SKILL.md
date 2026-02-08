---
name: Frontend Tech Stack
description: core technologies and libraries used in the frontend application
---

# Frontend Tech Stack

The frontend is built with a modern React stack focused on performance, type safety, and developer experience.

## Core Framework
- **React 19**: The library for web and native user interfaces.
- **TypeScript**: Statically typed superset of JavaScript.
- **Vite**: Next Generation Frontend Tooling (Bundler and Dev Server).

## Styling & UI
- **TailwindCSS 4**: Utility-first CSS framework.
- **Shadcn/UI**: Reusable components built with Radix UI and Tailwind CSS. Locate these in `src/components/ui`.
- **Lucide React**: Icon library.
- **Class Variance Authority (CVA)**: For creating variant-based component styles.
- **clsx / tailwind-merge**: Utilities for constructing `className` strings conditionally.

## State & Data Fetching
- **TanStack Query (React Query)**: For server state management and data fetching.
- **Axios**: HTTP client (wrapped by Orval generated hooks).

## Routing
- **React Router Dom v7**: Declarative routing for React.

## Forms
- **React Hook Form**: Performant, flexible and extensible forms with easy-to-use validation.
- **Zod**: TypeScript-first schema declaration and validation library.
- **@hookform/resolvers**: Zod resolver for React Hook Form.

## Testing
- **Vitest**: Blazing fast unit test framework.
- **React Testing Library**: Usage of `testing-library` to test components in a way that resembles how users find them.
