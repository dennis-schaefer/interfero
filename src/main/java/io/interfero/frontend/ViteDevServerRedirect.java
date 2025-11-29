package io.interfero.frontend;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@Profile("dev")
class ViteDevServerRedirect
{
    private static final String VITE_DEV_SERVER_URL = "http://localhost:3000/";

    @GetMapping("/")
    RedirectView redirectToViteDevServer()
    {
        return new RedirectView(VITE_DEV_SERVER_URL);
    }
}
