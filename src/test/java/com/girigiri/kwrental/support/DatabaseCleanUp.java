package com.girigiri.kwrental.support;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseCleanUp implements InitializingBean {

    private final DataSource dataSource;
    private final Set<String> tableNames;

    public DatabaseCleanUp(final DataSource dataSource) {
        this.dataSource = dataSource;
        this.tableNames = new HashSet<>();
    }

    @Override
    public void afterPropertiesSet() {
        callInTemplate(this::initTableNames);
    }

    private void initTableNames(final Statement statement) throws SQLException {
        statement.execute("SET REFERENTIAL_INTEGRITY FALSE");
        final ResultSet resultSet = statement.executeQuery(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'");
        while (resultSet.next()) {
            tableNames.add(resultSet.getString(1));
        }
    }

    @Transactional
    public void execute() {
        callInTemplate(this::truncateAllTables);
    }

    private void truncateAllTables(final Statement statement) throws SQLException {
        for (String tableName : tableNames) {
            statement.executeUpdate("TRUNCATE TABLE " + tableName);
        }
    }

    private void callInTemplate(final SQLThrowableConsumer<Statement> consumer) {
        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {
            consumer.consume(statement);
        } catch (SQLException e) {
            throw new RuntimeException("데이터 초기화 실패");
        }
    }
}

interface SQLThrowableConsumer<T> {
    void consume(T t) throws SQLException;
}
