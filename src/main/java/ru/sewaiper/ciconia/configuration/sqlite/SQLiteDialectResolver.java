package ru.sewaiper.ciconia.configuration.sqlite;

import org.springframework.data.jdbc.repository.config.DialectResolver.JdbcDialectProvider;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.lang.NonNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@SuppressWarnings("unused")
public class SQLiteDialectResolver implements JdbcDialectProvider {

    @NonNull
    @Override
    public Optional<Dialect> getDialect(@NonNull JdbcOperations operations) {
        return Optional.ofNullable(operations.execute(this::tryGetDialect));
    }

    private Dialect tryGetDialect(Connection connection) throws SQLException {
        var metadata = connection.getMetaData();
        String dbName = metadata.getDatabaseProductName().toLowerCase();
        if (dbName.contains("sqlite")) {
            return SQLiteDialect.INSTANCE;
        }
        return null;
    }
}
