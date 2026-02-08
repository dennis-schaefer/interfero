---
name: Frontend Routing & Auth
description: managing navigation and authentication state
---

# Frontend Routing & Auth

## Routing
Routing is handled by **React Router v7**.

- **Definition**: Routes are defined in `src/App.tsx` using `createBrowserRouter`.
- **Navigation**:
  - Use `<Link to="/path">` for declarative navigation.
  - Use `useNavigate()` hook for programmatic navigation.

## Authentication System

The application uses a persistent auth state managed via Context.

### Key Components

1. **`AuthProvider`** (`src/features/security/AuthProvider.tsx`):
   - Wraps the application.
   - Manages the current user's authentication status.
   - Likely exposes a context with values like `{ isAuthenticated, user, login, logout }`.

2. **`ProtectedPage`** (`src/features/security/ProtectedPage.tsx`):
   - A wrapper component for routes that require login.
   - Checks the auth context. If not authenticated, redirects to `/login`.

### Adding a New Protected Route

To add a new page that requires authentication:

1. Create the page component in `src/pages/`.
2. Add the route in `src/App.tsx`.
3. Wrap the element with `<ProtectedPage>`.

```tsx
// src/App.tsx
{
    path: "my-protected-feature",
    element: <ProtectedPage><MyFeaturePage /></ProtectedPage>
}
```

### Setup / Public Routes
Public routes (like `/login` or `/login/setup`) do not need the `ProtectedPage` wrapper. They might use specific layouts like `SetupLayout`.
