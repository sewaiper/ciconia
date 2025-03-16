package ru.sewaiper.ciconia.db;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import javax.sql.DataSource;
import java.util.List;

public class DataCleanerTestListener implements TestExecutionListener {

    private final List<String> tables = List.of(
            "packet_config", "packets_content", "packets",
            "messages", "subscriptions", "channels", "users"
    );

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        QueryLogger.disable();

        var ds = testContext.getApplicationContext().getBean(DataSource.class);

        var query = "delete from %s";

        try (var conn = ds.getConnection()) {
            conn.setAutoCommit(false);
            try (var statement = conn.createStatement()) {
                for (String table : tables) {
                    statement.executeUpdate(String.format(query, table));
                }
            }
            conn.commit();
        }
    }
}
