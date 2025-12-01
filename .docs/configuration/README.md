# Configuration 

## Database Settings
Properties for configuring a database connection to persist data. A database is optional but recommended for production 
deployments. 
Property prefix for the following properties: `interfero.database.`

| Property   | Type    | Default    | Description                                                               |
|------------|---------|------------|---------------------------------------------------------------------------|
| `enabled`  | boolean | `false`    | Whether to enable database persistence.                                   |
| `vendor`   | String  | `postgres` | The database vendor. Currently `postgres` and `timescaledb` are supported |
| `url`      | String  | -          | The JDBC URL for the database connection.                                 |
| `username` | String  | -          | The username for the database connection.                                 |
| `password` | String  | -          | The password for the database connection.                                 |

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
