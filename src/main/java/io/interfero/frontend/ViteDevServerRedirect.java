package io.interfero.frontend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@Controller
@Profile("dev")
@RequiredArgsConstructor
class ViteDevServerRedirect
{
    private final ViteConfiguration viteConfiguration;

    @GetMapping("/")
    RedirectView redirectToViteDevServer()
    {
        log.debug("Redirecting to Vite Dev Server at {}", viteConfiguration.getDevServerUrl());
        return new RedirectView(viteConfiguration.getDevServerUrl());
    }

    @GetMapping("/login")
    RedirectView redirectToViteDevServerLogin()
    {
        log.debug("Redirecting to Vite Dev Server login at {}", viteConfiguration.getDevServerUrl() + "/login");
        return new RedirectView(viteConfiguration.getDevServerUrl() + "/login");
    }
}
