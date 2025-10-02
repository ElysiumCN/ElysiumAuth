package com.skilfully.elysium.elysium_auth.database;

import com.skilfully.elysium.elysium_auth.database.config.DatabaseConfig;
import com.skilfully.elysium.elysium_auth.database.config.DatabaseType;
import com.skilfully.elysium.elysium_auth.database.dao.AccountDataDAO;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.transaction.TransactionException;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public class DatabaseManager {

    private boolean initialized = false;
    @Getter
    private DatabaseConfig databaseConfig;
    private HikariDataSource dataSource;
    private Jdbi jdbi;
    private ScheduledExecutorService healthCheckScheduler;

    // 单例模式
    private static DatabaseManager instance;

    private DatabaseManager() {}

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void init(
            String type,
            String connectionUrl,
            @Nullable String username,
            @Nullable String password,
            @Nullable Long maxLifetime,
            @Nullable Integer maxPoolSize,
            @Nullable Integer minIdle,
            @Nullable Boolean cachePreparedStatements,
            @Nullable Integer preparedStatementCacheSize,
            @Nullable Integer preparedStatementCacheSqlLimit
    ) {
        if (initialized) {
            throw new IllegalStateException("数据库无法被再次初始化！");
        }

        // 构建配置对象
        this.databaseConfig = buildDatabaseConfig(type, connectionUrl, username, password,
                maxLifetime, maxPoolSize, minIdle, cachePreparedStatements,
                preparedStatementCacheSize, preparedStatementCacheSqlLimit);

        // 初始化数据库连接
        initializeDatabase();

        this.initialized = true;
        //TODO：记录日志到文件.info("数据库初始化成功，类型: {}", type);
    }

    private DatabaseConfig buildDatabaseConfig(
            String type,
            String connectionUrl,
            @Nullable String username,
            @Nullable String password,
            @Nullable Long maxLifetime,
            @Nullable Integer maxPoolSize,
            @Nullable Integer minIdle,
            @Nullable Boolean cachePreparedStatements,
            @Nullable Integer preparedStatementCacheSize,
            @Nullable Integer preparedStatementCacheSqlLimit
    ) {
        DatabaseConfig.DatabaseConfigBuilder builder = DatabaseConfig.builder()
                .type(DatabaseType.fromString(type))
                .connectionUrl(buildJdbcUrl(type, connectionUrl))
                .username(username)
                .password(password);

        // 设置可选参数（如果提供）
        if (maxLifetime != null) builder.maxLifetime(maxLifetime);
        if (maxPoolSize != null) builder.maxPoolSize(maxPoolSize);
        if (minIdle != null) builder.minIdle(minIdle);
        if (cachePreparedStatements != null) builder.cachePreparedStatements(cachePreparedStatements);
        if (preparedStatementCacheSize != null) builder.preparedStatementCacheSize(preparedStatementCacheSize);
        if (preparedStatementCacheSqlLimit != null) builder.preparedStatementCacheSqlLimit(preparedStatementCacheSqlLimit);

        return builder.build();
    }

    private String buildJdbcUrl(String type, String connectionUrl) {
        if (connectionUrl.toLowerCase().startsWith("jdbc:")) {
            return connectionUrl;
        }

        DatabaseType dbType = DatabaseType.fromString(type);
        return switch (dbType) {
            case MYSQL -> "jdbc:mysql://" + connectionUrl;
            case SQLITE -> "jdbc:sqlite:" + connectionUrl;
            case POSTGRESQL -> "jdbc:postgresql://" + connectionUrl;
        };
    }

    private void initializeDatabase() {
        try {
            //TODO：记录日志到文件.info("正在初始化数据库连接: {}", databaseConfig.getType());

            HikariConfig hikariConfig = createHikariConfig();
            this.dataSource = new HikariDataSource(hikariConfig);
            this.jdbi = Jdbi.create(dataSource);
            jdbi.installPlugin(new SqlObjectPlugin());

            startHealthCheck();
            testConnection();
            initializeTables();

            //TODO：记录日志到文件.info("数据库连接初始化成功: {}", databaseConfig.getConnectionUrl());
        } catch (Exception e) {
            throw new RuntimeException("数据库初始化失败", e);
        }
    }

    private HikariConfig createHikariConfig() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(databaseConfig.getConnectionUrl());
        config.setAutoCommit(databaseConfig.isAutoCommit());

        // 只有提供了用户名和密码时才设置
        if (databaseConfig.getUsername() != null) {
            config.setUsername(databaseConfig.getUsername());
        }
        if (databaseConfig.getPassword() != null) {
            config.setPassword(databaseConfig.getPassword());
        }

        // 连接池配置
        config.setMaximumPoolSize(databaseConfig.getMaxPoolSize());
        config.setMinimumIdle(databaseConfig.getMinIdle());
        config.setMaxLifetime(databaseConfig.getMaxLifetime());
        config.setConnectionTimeout(databaseConfig.getConnectionTimeout());
        config.setIdleTimeout(databaseConfig.getIdleTimeout());
        config.setPoolName("HikariPool-" + databaseConfig.getType());

        // 数据库特定配置
        configureDatabaseSpecificSettings(config);

        return config;
    }

    private void configureDatabaseSpecificSettings(HikariConfig config) {
        switch (databaseConfig.getType()) {
            case SQLITE:
                config.setMaximumPoolSize(1);
                config.setConnectionTestQuery("SELECT 1");
                config.addDataSourceProperty("foreign_keys", "ON");
                config.addDataSourceProperty("journal_mode", "WAL");
                config.addDataSourceProperty("synchronous", "NORMAL");
                break;

            case MYSQL:
                config.setConnectionTestQuery("SELECT 1");
                // PreparedStatement缓存配置
                if (databaseConfig.isCachePreparedStatements()) {
                    config.addDataSourceProperty("cachePrepStmts", "true");
                    config.addDataSourceProperty("prepStmtCacheSize", databaseConfig.getPreparedStatementCacheSize());
                    config.addDataSourceProperty("prepStmtCacheSqlLimit", databaseConfig.getPreparedStatementCacheSqlLimit());
                    config.addDataSourceProperty("useServerPrepStmts", "true");
                    config.addDataSourceProperty("useLocalSessionState", "true");
                    config.addDataSourceProperty("rewriteBatchedStatements", "true");
                    config.addDataSourceProperty("cacheResultSetMetadata", "true");
                    config.addDataSourceProperty("cacheServerConfiguration", "true");
                    config.addDataSourceProperty("elideSetAutoCommits", "true");
                    config.addDataSourceProperty("maintainTimeStats", "false");
                }
                break;

            case POSTGRESQL:
                config.setConnectionTestQuery("SELECT 1");
                config.addDataSourceProperty("ApplicationName", "Elysium-Auth");
                config.addDataSourceProperty("tcpKeepAlive", "true");
                // PostgreSQL PreparedStatement缓存
                if (databaseConfig.isCachePreparedStatements()) {
                    config.addDataSourceProperty("preparedStatementCacheQueries", databaseConfig.getPreparedStatementCacheSize());
                    config.addDataSourceProperty("preparedStatementCacheSizeMiB", 5);
                }
                break;

            default:
                config.setConnectionTestQuery("SELECT 1");
                break;
        }
    }

    private void initializeTables() {
        try {
            String createTableSQL = getCreateTableSQL();
            jdbi.useHandle(handle -> {
                // 执行表创建SQL
                handle.createScript(createTableSQL).executeAsSeparateStatements();
                //TODO：记录日志到文件.info("数据库表初始化成功");
            });
        } catch (Exception e) {
            throw new RuntimeException("数据库表初始化失败", e);
        }
    }

    private String getCreateTableSQL() {
        return switch (databaseConfig.getType()) {
            case SQLITE -> """
                    CREATE TABLE IF NOT EXISTS account (
                        uuid VARCHAR(36) PRIMARY KEY,
                        name VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        email VARCHAR(255),
                        mobile VARCHAR(20),
                        registerDate DATETIME NOT NULL,
                        offlineLocationX DOUBLE NOT NULL,
                        offlineLocationY DOUBLE NOT NULL,
                        offlineLocationZ DOUBLE NOT NULL,
                        offlineLocationYaw DOUBLE NOT NULL,
                        offlineLocationPitch DOUBLE NOT NULL,
                        ban BOOLEAN DEFAULT FALSE,
                        banReason TEXT,
                        banTime DATETIME,
                        banner VARCHAR(255)
                    );
                    CREATE INDEX IF NOT EXISTS idx_account_name ON account(name);
                    CREATE INDEX IF NOT EXISTS idx_account_email ON account(email);
                    CREATE INDEX IF NOT EXISTS idx_account_ban ON account(ban);
                    """;
            case MYSQL -> """
                    CREATE TABLE IF NOT EXISTS account (
                        uuid VARCHAR(36) PRIMARY KEY,
                        name VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        email VARCHAR(255),
                        mobile VARCHAR(20),
                        registerDate DATETIME NOT NULL,
                        offlineLocationX DOUBLE NOT NULL,
                        offlineLocationY DOUBLE NOT NULL,
                        offlineLocationZ DOUBLE NOT NULL,
                        offlineLocationYaw DOUBLE NOT NULL,
                        offlineLocationPitch DOUBLE NOT NULL,
                        ban BOOLEAN DEFAULT FALSE,
                        banReason TEXT,
                        banTime DATETIME,
                        banner VARCHAR(255),
                        INDEX idx_name (name),
                        INDEX idx_email (email),
                        INDEX idx_ban (ban)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
                    """;
            case POSTGRESQL -> """
                    CREATE TABLE IF NOT EXISTS account (
                        uuid VARCHAR(36) PRIMARY KEY,
                        name VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        email VARCHAR(255),
                        mobile VARCHAR(20),
                        registerDate TIMESTAMP NOT NULL,
                        offlineLocationX DOUBLE PRECISION NOT NULL,
                        offlineLocationY DOUBLE PRECISION NOT NULL,
                        offlineLocationZ DOUBLE PRECISION NOT NULL,
                        offlineLocationYaw DOUBLE PRECISION NOT NULL,
                        offlineLocationPitch DOUBLE PRECISION NOT NULL,
                        ban BOOLEAN DEFAULT FALSE,
                        banReason TEXT,
                        banTime TIMESTAMP,
                        banner VARCHAR(255)
                    );
                    CREATE INDEX IF NOT EXISTS idx_account_name ON account(name);
                    CREATE INDEX IF NOT EXISTS idx_account_email ON account(email);
                    CREATE INDEX IF NOT EXISTS idx_account_ban ON account(ban);
                    """;
        };
    }

    private void startHealthCheck() {
        healthCheckScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Database-HealthCheck");
            t.setDaemon(true);
            return t;
        });

        healthCheckScheduler.scheduleAtFixedRate(() -> {
            try {
                if (dataSource != null && !dataSource.isClosed()) {
                    jdbi.withHandle(handle ->
                            handle.createQuery("SELECT 1").mapTo(Integer.class).one());
                    //TODO：记录日志到文件.trace("数据库健康检查通过");
                }
            } catch (Exception e) {
                //TODO：记录日志到文件.error("数据库健康检查失败", e);
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    private void testConnection() {
        try {
            Integer result = jdbi.withHandle(handle ->
                    handle.createQuery("SELECT 1").mapTo(Integer.class).one());
            //TODO：记录日志到文件.info("数据库连接测试成功: {}", result);
        } catch (Exception e) {
            throw new RuntimeException("数据库连接测试失败", e);
        }
    }

    // ============ 事务支持方法 ============

    /**
     * 在事务中执行操作（无返回值）
     */
    public void executeInTransaction(Consumer<AccountDataDAO> operations) {
        if (!initialized) {
            throw new IllegalStateException("数据库未初始化，请先调用init()方法");
        }

        jdbi.useTransaction(handle -> {
            AccountDataDAO dao = handle.attach(AccountDataDAO.class);
            operations.accept(dao);
        });
    }

    /**
     * 在事务中执行操作（有返回值）
     */
    public <T> T executeInTransaction(Function<AccountDataDAO, T> operations) {
        if (!initialized) {
            throw new IllegalStateException("数据库未初始化，请先调用init()方法");
        }

        return jdbi.inTransaction(handle -> {
            AccountDataDAO dao = handle.attach(AccountDataDAO.class);
            return operations.apply(dao);
        });
    }

    /**
     * 手动管理的事务
     */
    public void executeInManualTransaction(TransactionCallback callback) {
        if (!initialized) {
            throw new IllegalStateException("数据库未初始化，请先调用init()方法");
        }

        jdbi.useHandle(handle -> {
            try {
                handle.begin();
                callback.execute(handle.attach(AccountDataDAO.class), handle);
                handle.commit();
            } catch (Exception e) {
                handle.rollback();
                throw new TransactionException("事务执行失败，已回滚", e);
            }
        });
    }

    @FunctionalInterface
    public interface TransactionCallback {
        void execute(AccountDataDAO dao, org.jdbi.v3.core.Handle handle) throws Exception;
    }

    // ============ 公共方法 ============

    public Jdbi getJdbi() {
        if (!initialized) {
            throw new IllegalStateException("数据库未初始化，请先调用init()方法");
        }
        return jdbi;
    }

    public HikariDataSource getDataSource() {
        if (!initialized) {
            throw new IllegalStateException("数据库未初始化，请先调用init()方法");
        }
        return dataSource;
    }

    public boolean isInitialized() {
        return initialized && dataSource != null && !dataSource.isClosed();
    }

    public AccountDataDAO getAccountDataDAO() {
        if (!initialized) {
            throw new IllegalStateException("数据库未初始化，请先调用init()方法");
        }
        return jdbi.onDemand(AccountDataDAO.class);
    }

    public void close() {
        //TODO：记录日志到文件.info("正在关闭数据库连接...");

        if (healthCheckScheduler != null) {
            healthCheckScheduler.shutdown();
            try {
                if (!healthCheckScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    healthCheckScheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                healthCheckScheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            initialized = false;
            //TODO：记录日志到文件.info("数据库连接已关闭");
        }
    }

    // ============ 便捷初始化方法 ============

    public void initSimple(String type, String connectionUrl) {
        init(type, connectionUrl, null, null, null, null, null, null, null, null);
    }

    public void initWithAuth(String type, String connectionUrl, String username, String password) {
        init(type, connectionUrl, username, password, null, null, null, null, null, null);
    }

    public void initWithCache(String type, String connectionUrl, String username, String password) {
        init(type, connectionUrl, username, password, null, null, null,
                true, 250, 2048);
    }
}