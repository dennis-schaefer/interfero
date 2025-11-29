package io.interfero;

import io.interfero.frontend.StaticResourcesConfiguration;
import io.interfero.frontend.ViteConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ViteConfiguration.class, StaticResourcesConfiguration.class})
public class InterferoApplication
{
    static void main(String[] args)
    {
        SpringApplication.run(InterferoApplication.class, args);
    }
}
