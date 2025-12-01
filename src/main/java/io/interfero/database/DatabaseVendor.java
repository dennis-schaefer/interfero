package io.interfero.database;

public enum DatabaseVendor
{
    postgres,
    timescaledb;

    String getMasterChangelogPath()
    {
        if (this == timescaledb)
            return "classpath:db/changelog/timescaledb/db.changelog-master.yaml";

        return "classpath:db/changelog/postgres/db.changelog-master.yaml";
    }
}
