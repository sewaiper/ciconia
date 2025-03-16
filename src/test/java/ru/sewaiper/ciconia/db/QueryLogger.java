package ru.sewaiper.ciconia.db;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.github.vertical_blank.sqlformatter.languages.Dialect;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.QueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.SystemOutQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;
import java.util.concurrent.atomic.AtomicBoolean;

public class QueryLogger implements BeanPostProcessor {

    private static final AtomicBoolean IS_LOG = new AtomicBoolean(false);

    public static void enable() {
        IS_LOG.set(true);
    }

    public static void disable() {
        IS_LOG.set(false);
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean,
                                                 @NonNull String beanName) throws BeansException {

        if (bean instanceof DataSource ds) {
            return ProxyDataSourceBuilder.create(ds)
                    .listener(sysOutLogListener())
                    .build();
        }
        return bean;
    }

    private QueryExecutionListener sysOutLogListener() {
        var listener = new SystemOutQueryLoggingListener() {
            @Override
            protected void writeLog(String message) {
                if (IS_LOG.get()) {
                    super.writeLog(message);
                }
            }
        };

        listener.setQueryLogEntryCreator(getEntryCreator());

        return listener;
    }

    private QueryLogEntryCreator getEntryCreator() {
        return new DefaultQueryLogEntryCreator() {
            @Override
            protected String formatQuery(String query) {
                return SqlFormatter.of(Dialect.PostgreSql).format(query);
            }
        };
    }
}
