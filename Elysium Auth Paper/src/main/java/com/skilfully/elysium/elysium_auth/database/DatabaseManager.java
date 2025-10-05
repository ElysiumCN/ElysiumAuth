package com.skilfully.elysium.elysium_auth.database;

import com.skilfully.elysium.elysium_auth.data.GlobalData;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {
    // 数据库类型枚举
    public enum DatabaseType {
        MYSQL, SQLITE, POSTGRESQL
    }

    private static DatabaseManager instance;
    private HikariDataSource dataSource;
    private DatabaseType dbType;

    // 私有构造函数，防止外部实例化
    private DatabaseManager() {}

    // 单例模式获取实例
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * 初始化数据库连接池
     * @param type 数据库类型
     * @param config 数据库配置参数
     * @return 是否初始化成功
     */
    public boolean initialize(DatabaseType type, Map<String, String> config) {
        this.dbType = type;
        HikariConfig hikariConfig = new HikariConfig();

        try {
            // 从配置中获取SSL设置，默认为false
            boolean useSSL = Boolean.parseBoolean(config.getOrDefault("useSSL", "false"));

            // 根据数据库类型配置连接池
            switch (type) {
                case MYSQL:
                    String mysqlUrl = String.format(
                            "jdbc:mysql://%s/%s?useSSL=%b&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                            config.getOrDefault("address", "localhost"),
                            config.getOrDefault("database", "elysium_auth"),
                            useSSL
                    );

                    // 如果使用SSL，添加额外的SSL配置
                    if (useSSL) {
                        mysqlUrl += "&requireSSL=true&verifyServerCertificate=false";
                    }

                    hikariConfig.setJdbcUrl(mysqlUrl);
                    hikariConfig.setUsername(config.get("username"));
                    hikariConfig.setPassword(config.get("password"));
                    hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
                    hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
                    hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                    break;

                case SQLITE:
                    // SQLite 通常不需要SSL配置
                    String sqlitePath = config.getOrDefault("path", "data.db");
                    hikariConfig.setJdbcUrl("jdbc:sqlite:" + GlobalData.dataFolder + sqlitePath);
                    hikariConfig.setMaximumPoolSize(1);
                    hikariConfig.setConnectionTestQuery("SELECT 1");
                    break;

                case POSTGRESQL:
                    String postgresUrl = String.format(
                            "jdbc:postgresql://%s/%s",
                            config.getOrDefault("address", "localhost"),
                            config.getOrDefault("database", "elysium_auth")
                    );

                    // PostgreSQL SSL配置
                    if (useSSL) {
                        postgresUrl += "?ssl=true&sslmode=require";
                        // 可以添加更多SSL相关配置
                        hikariConfig.addDataSourceProperty("sslmode", "require");
                        hikariConfig.addDataSourceProperty("ssl", "true");
                    } else {
                        postgresUrl += "?ssl=false";
                        hikariConfig.addDataSourceProperty("ssl", "false");
                    }

                    hikariConfig.setJdbcUrl(postgresUrl);
                    hikariConfig.setUsername(config.get("username"));
                    hikariConfig.setPassword(config.get("password"));
                    hikariConfig.addDataSourceProperty("defaultRowFetchSize", "100");

                    // 额外的SSL配置
                    if (useSSL) {
                        // 如果需要自定义SSL工厂或其他SSL参数
                        String sslFactory = config.get("sslFactory");
                        if (sslFactory != null) {
                            hikariConfig.addDataSourceProperty("sslFactory", sslFactory);
                        }

                        String sslCert = config.get("sslCert");
                        if (sslCert != null) {
                            hikariConfig.addDataSourceProperty("sslCert", sslCert);
                        }
                    }
                    break;
            }

            // 通用连接池配置
            hikariConfig.setMaximumPoolSize(10); // 最大连接数
            hikariConfig.setMinimumIdle(2);      // 最小空闲连接
            hikariConfig.setIdleTimeout(300000); // 连接空闲超时时间(5分钟)
            hikariConfig.setMaxLifetime(1800000); // 连接最大生命周期(30分钟)
            hikariConfig.setConnectionTimeout(30000); // 获取连接超时时间(30秒)

            // 初始化数据源
            dataSource = new HikariDataSource(hikariConfig);

            // 测试连接
            try (Connection conn = getConnection()) {
                return conn != null && !conn.isClosed();
            }
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
            if (dataSource != null) {
                dataSource.close();
            }
        }
        return false;
    }

    /**
     * 从连接池获取数据库连接
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("数据库连接池尚未初始化，请先调用initialize方法");
        }
        return dataSource.getConnection();
    }

    /**
     * 执行查询语句
     */
    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = prepareStatement(sql, params);
        return stmt.executeQuery();
    }

    /**
     * 执行更新语句（INSERT/UPDATE/DELETE）
     */
    public int executeUpdate(String sql, Object... params) throws SQLException {
        try (PreparedStatement stmt = prepareStatement(sql, params)) {
            return stmt.executeUpdate();
        }
    }

    /**
     * 准备带参数的SQL语句
     */
    private PreparedStatement prepareStatement(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        return stmt;
    }

    /**
     * 执行创建表等DDL语句
     */
    public boolean executeDDL(String sql) throws SQLException {
        try (Statement stmt = getConnection().createStatement()) {
            return stmt.execute(sql);
        }
    }

    /**
     * 关闭数据库连接池
     */
    public void close() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }

    /**
     * 根据数据库类型获取对应的SQL语法
     */
    public String getSqlSyntax(String key) {
        // 不同数据库的SQL语法差异映射
        Map<String, Map<String, String>> syntaxMap = new HashMap<>();

        // MySQL特定语法
        Map<String, String> mysqlSyntax = new HashMap<>();
        mysqlSyntax.put("AUTO_INCREMENT", "AUTO_INCREMENT");
        mysqlSyntax.put("SERIAL", "BIGINT AUTO_INCREMENT PRIMARY KEY");
        syntaxMap.put("MYSQL", mysqlSyntax);

        // SQLite特定语法
        Map<String, String> sqliteSyntax = new HashMap<>();
        sqliteSyntax.put("AUTO_INCREMENT", "AUTOINCREMENT");
        sqliteSyntax.put("SERIAL", "INTEGER PRIMARY KEY AUTOINCREMENT");
        syntaxMap.put("SQLITE", sqliteSyntax);

        // PostgreSQL特定语法
        Map<String, String> pgSyntax = new HashMap<>();
        pgSyntax.put("AUTO_INCREMENT", "SERIAL");
        pgSyntax.put("SERIAL", "SERIAL PRIMARY KEY");
        syntaxMap.put("POSTGRESQL", pgSyntax);

        return syntaxMap.get(dbType.toString()).getOrDefault(key, key);
    }
}