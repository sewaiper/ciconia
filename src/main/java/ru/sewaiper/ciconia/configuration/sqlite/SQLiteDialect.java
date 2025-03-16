package ru.sewaiper.ciconia.configuration.sqlite;

import org.springframework.data.relational.core.dialect.AbstractDialect;
import org.springframework.data.relational.core.dialect.ArrayColumns;
import org.springframework.data.relational.core.dialect.LimitClause;
import org.springframework.data.relational.core.dialect.LockClause;
import org.springframework.data.relational.core.sql.LockOptions;
import org.springframework.lang.NonNull;

public class SQLiteDialect extends AbstractDialect {

    public static final SQLiteDialect INSTANCE = new SQLiteDialect();

    private SQLiteDialect() {
    }

    @Override
    @NonNull
    public LockClause lock() {
        return new LockClause() {

            @NonNull
            @Override
            public String getLock(@NonNull LockOptions lockOptions) {
                return "WITH LOCK";
            }

            @NonNull
            @Override
            public Position getClausePosition() {
                return Position.AFTER_ORDER_BY;
            }
        };
    }

    @NonNull
    @Override
    public ArrayColumns getArraySupport() {
        return ArrayColumns.Unsupported.INSTANCE;
    }

    @NonNull
    @Override
    public LimitClause limit() {
        return new LimitClause() {

            @NonNull
            @Override
            public String getLimit(long limit) {
                return "LIMIT " + limit;
            }

            @NonNull
            @Override
            public String getOffset(long offset) {
                return String.format("LIMIT %d, 18446744073709551615", offset);
            }

            @NonNull
            @Override
            public String getLimitOffset(long limit, long offset) {

                return String.format("LIMIT %s, %s", offset, limit);
            }

            @NonNull
            @Override
            public Position getClausePosition() {
                return Position.AFTER_ORDER_BY;
            }
        };
    }
}
