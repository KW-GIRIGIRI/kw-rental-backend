package com.girigiri.kwrental.common.log;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import jakarta.annotation.PostConstruct;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Locale;

@Configuration
public class P6spySqlFormatter  implements MessageFormattingStrategy {
    @PostConstruct
    public void setLogMessageFormat() {
        P6SpyOptions.getActiveInstance().setLogMessageFormat(this.getClass().getName());
    }

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        sql = formatSql(category, sql);
        return String.format("[%s] | %d ms | %s", category, elapsed, sql);
    }

    private String formatSql(String category, String sql) {
        if (sql != null && !sql.trim().isEmpty() && Category.STATEMENT.getName().equals(category)) {
            String trimmedSQL = sql.trim().toLowerCase(Locale.ROOT);
            if (trimmedSQL.startsWith("create") || trimmedSQL.startsWith("alter") || trimmedSQL.startsWith("comment")) {
                sql = FormatStyle.DDL.getFormatter().format(sql);
            } else {
                sql = FormatStyle.BASIC.getFormatter().format(sql);
            }
            return threadInfo() + sql;
        }
        return sql;
    }

    private String threadInfo() {
        final Thread thread = Thread.currentThread();
        final Long id = thread.getId();
        final String call = lastCallOf(thread);
        return String.format("ID : %d, caused by %s\n", id, call);
    }

    private String lastCallOf(final Thread thread) {
        return Arrays.stream(thread.getStackTrace())
                .filter(it -> it.toString().startsWith("com.girigiri") && !it.toString().contains(ClassUtils.getUserClass(this).getName()))
                .findFirst()
                .map(StackTraceElement::toString)
                .orElse("cannot fount call");
    }
}
