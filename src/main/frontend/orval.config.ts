import { defineConfig } from 'orval';

export default defineConfig({
    interfero: {
        input: 'http://localhost:8080/api-docs/v3',
        output: {
            mode: 'tags',
            namingConvention: 'PascalCase',
            target: 'src/api/endpoints',
            schemas: 'src/api/schemas',
            client: 'react-query',
            override: {
                mutator: {
                    path: './src/api/axios-client.ts',
                    name: 'axiosInstance',
                },
            },
        },
    },
});