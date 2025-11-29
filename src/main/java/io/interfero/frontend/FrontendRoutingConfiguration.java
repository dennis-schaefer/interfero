package io.interfero.frontend;

import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import java.util.List;

import static java.util.Objects.nonNull;

@Configuration
class FrontendRoutingConfiguration implements WebMvcConfigurer
{
    /**
     * This method is required to forward all routes to index.html, so that the frontend router can handle them and
     * forward the request to the desired view.
     *
     * @param registry the resource handler registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        var endpoint = "/";
        var location = "classpath:/static/";

        var endpointPatterns = new String[] {endpoint, endpoint + "**"};
        registry
                .addResourceHandler(endpointPatterns)
                .addResourceLocations(location)
                .resourceChain(false)
                .addResolver(new PathResourceResolver()
                {
                    public Resource resolveResource(HttpServletRequest request,
                                                    @NonNull String requestPath,
                                                    @NonNull List<? extends Resource> locations,
                                                    @NonNull ResourceResolverChain chain)
                    {
                        var resource = super.resolveResource(request, requestPath, locations, chain);
                        if (nonNull(resource))
                            return resource;

                        return super.resolveResource(request, "/index.html", locations, chain);
                    }
                });
    }
}
