import { defineConfig } from 'orval';

export default defineConfig({
    todoApp: {
        input: 'http://localhost:8080/api-docs/v3',
        output: {
            mode: 'tags-split',
            target: 'src/api/endpoints',
            schemas: 'src/api/model',
            client: 'react-query',
            override: {
                mutator: {
                    path: './src/api/axios-client.ts',
                    name: 'customInstance',
                },
            },
        },
    },
});