package io.interfero;

import io.interfero.frontend.StaticResourcesConfiguration;
import io.interfero.frontend.ViteConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableConfigurationProperties({ViteConfiguration.class, StaticResourcesConfiguration.class})
public class InterferoApplication
{
    static void main(String[] args)
    {
        SpringApplication.run(InterferoApplication.class, args);
    }

    @RestController
    @RequestMapping("/api/hello")
    static class HelloController
    {
        @GetMapping
        public String hello()
        {
            return "Hello Intefero! 🌊";
        }
    }
}
