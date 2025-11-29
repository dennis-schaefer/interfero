# Configuration 

## Frontend Development
Properties for configuring frontend development settings.  
Property prefix for the following properties: `interfero.frontend.`

| Property                            | Type    | Default                     | Description                                                                                  |
|-------------------------------------|---------|-----------------------------|----------------------------------------------------------------------------------------------|
| `vite.autostart-enabled`            | boolean | `true`                      | Whether to automatically start the Vite dev server with the application in development mode. |
| `vite.dev-server-url`               | String  | `http://localhost:3000`     | The URL of the Vite development server.                                                      |
| `vite.directory`                    | String  | `src/main/frontend`         | The directory where the Vite and the frontend application is located.                        |
| `vite.startup-command`              | String  | `npm run dev`               | The command to start the Vite development server.                                            |
| `static-resources.directory`        | String  | `src/main/resources/static` | The path to the static resources directory.                                                  |
| `static-resources.clean-on-startup` | boolean | `true`                      | Whether to clean up static resources on application startup.                                 |
