package io.interfero.database;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Slf4j
@Component
class DisabledDatabaseWarning
{
    private final DataSource dataSource;

    DisabledDatabaseWarning(@Autowired(required = false) DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void warnIfDatabaseDisabled()
    {
        if (dataSource == null)
            log.warn("No database configured! Interfero will run in a limited mode without persistence.");
    }
}
