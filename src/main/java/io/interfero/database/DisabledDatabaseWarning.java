package io.interfero.database;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "interfero.database.enabled", havingValue = "false")
class DisabledDatabaseWarning
{
    @PostConstruct
    public void warnThatDatabaseDisabled()
    {
        log.warn("No database configured! Interfero will run in a limited mode without persistence.");
    }
}
