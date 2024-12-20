package com.halfacode.flyway_spring.flywayLarge;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class V4__Migrate_large_dataset extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO users (username) VALUES (?)")) {
            for (int i = 0; i < 1000000; i++) {
                stmt.setString(1, "user" + i);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
}
