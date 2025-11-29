package io.interfero.frontend;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class cleans the static resources directory before the application starts in development mode.
 * The purpose is to ensure that stale static files do not interfere with the development process. The static directory
 * will be repopulated by the frontend build process as needed.
 */
@Slf4j
@Component
@Profile("dev")
class StaticResourcesCleanup
{
    private static final String STATIC_RESOURCES_DIRECTORY = "src/main/resources/static";

    @PostConstruct
    void cleanStaticResources() throws IOException
    {
        log.info("Cleaning static resources directory: {}", STATIC_RESOURCES_DIRECTORY);
        var staticResourcesPath = Paths.get(STATIC_RESOURCES_DIRECTORY);

        if (Files.exists(staticResourcesPath) && Files.isDirectory(staticResourcesPath))
            deleteDirectoryContents(staticResourcesPath);
    }

    private void deleteDirectoryContents(Path directory) throws IOException
    {
        try (var files = Files.newDirectoryStream(directory))
        {
            for (var entry : files)
            {
                if (Files.isDirectory(entry))
                {
                    deleteDirectoryContents(entry);
                    return;
                }

                log.debug("Deleting file: {}", entry);
                Files.delete(entry);
            }
        }

        log.debug("Deleting directory: {}", directory);
        Files.delete(directory);
    }
}
