package io.interfero.frontend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
class ViteDevServerStartup implements SmartLifecycle
{
    private final ViteConfiguration viteConfiguration;
    private Process viteProcess;
    private boolean isRunning = false;

    @Override
    public void start()
    {
        if (!viteConfiguration.isAutostartEnabled())
            return;

        log.info("Starting Vite Dev Server...");

        var command = new String[] { "sh", "-c", viteConfiguration.getStartupCommand() };
        var isWindows = System.getProperty("os.name").toLowerCase().startsWith("win");
        if (isWindows)
            command = new String[] { "cmd.exe", "/c", viteConfiguration.getStartupCommand() };

        try
        {
            var builder = new ProcessBuilder();
            builder.command(command);
            builder.directory(new File(viteConfiguration.getDirectory()));
            builder.inheritIO();

            viteProcess = builder.start();
            isRunning = true;

            log.info("Vite Dev Server started with PID: {}", viteProcess.pid());
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to start Vite Dev Server", e);
        }
    }

    @Override
    public void stop()
    {
        if (viteProcess == null || !viteProcess.isAlive())
            return;

        this.viteProcess.toHandle().descendants().forEach(handle ->
        {
            log.debug("Stopping Vite Dev-Sever Sub-Process PID: {}", handle.pid());
            handle.destroy();
        });

        log.info("Stopping Vite Dev-Server...");
        this.viteProcess.destroy();

        try
        {
            if (!this.viteProcess.waitFor(5, TimeUnit.SECONDS))
            {
                log.warn("Vite Process is not responding - Stopping forcefully...");
                this.viteProcess.toHandle().descendants().forEach(ProcessHandle::destroyForcibly);
                this.viteProcess.destroyForcibly();
            }
        }
        catch (InterruptedException e)
        {
            this.viteProcess.destroyForcibly();
            Thread.currentThread().interrupt();
        }

        isRunning = false;
    }

    @Override
    public boolean isRunning()
    {
        return isRunning;
    }
}
