---
name: Frontend API Integration
description: instructions for working with the API using Orval and React Query
---

# Frontend API Integration

The frontend communicates with the backend using a generated API client managed by **Orval**. This ensures type safety and synchronicity with the backend OpenAPI specification.

## Overview
- **Generator**: [Orval](https://orval.dev/)
- **Input**: OpenAPI v3 spec from `http://localhost:8080/api-docs/v3` (local backend).
- **Output**: React Query hooks and TypeScript schemas.
- **Location**: `src/api/endpoints` (Hooks) and `src/api/schemas` (Types).

## Workflow

### 1. Regenerating the Client
When backend endpoints change, you must regenerate the frontend client code:
```bash
npm run generate:api
# Or effectively during dev start:
npm run dev
```

### 2. Using the API
Do **not** use `axios` directly for fetching data. Use the generated hooks.

**Example:**
If the backend has an endpoint `GET /api/clusters`, Orval generates a hook `useGetClusters`.

```tsx
import { useGetClusters } from '@/api/endpoints/cluster-controller';

export const ClusterList = () => {
  const { data, isLoading, error } = useGetClusters();

  if (isLoading) return <div>Loading...</div>;
  if (error) return <div>Error loading clusters</div>;

  return (
    <ul>
      {data?.map(cluster => (
        <li key={cluster.id}>{cluster.name}</li>
      ))}
    </ul>
  );
};
```

### 3. Mutations (POST, PUT, DELETE)
For modifying data, use the generated mutation hooks.

```tsx
import { useCreateCluster } from '@/api/endpoints/cluster-controller';

const { mutate } = useCreateCluster();

const handleSubmit = (data) => {
    mutate({ data }); // 'data' matches the request body schema
};
```

## Configuration
- **Config File**: `orval.config.ts`.
- **Custom Client**: The generated hooks use a custom Axios instance defined in `src/api/axios-client.ts`.
    - This instance handles base configuration and request cancellation.
    - It is wired up via the `mutator` setting in `orval.config.ts`.

## Authentication & Security
- **CSRF**: The application fetches a CSRF token from `/api/csrf` when the app mounts (see `src/App.tsx` global effect).
- **Cookies**: Authentication relies on HttpOnly cookies set by the backend. The Axios instance automatically includes credentials (cookies) if the backend domain matches or if `withCredentials` is set (default for same-origin).
