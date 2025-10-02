package com.skilfully.elysium.elysium_auth.database.config;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DatabaseConfig {
    DatabaseType type;
    String connectionUrl;
    String username;
    String password;

    // 连接池配置
    @Builder.Default
    int maxPoolSize = 20;
    @Builder.Default
    int minIdle = 5;
    @Builder.Default
    long connectionTimeout = 30000;
    @Builder.Default
    long idleTimeout = 600000;
    @Builder.Default
    long maxLifetime = 1800000;

    // 缓存配置
    @Builder.Default
    boolean cachePreparedStatements = true;
    @Builder.Default
    int preparedStatementCacheSize = 250;
    @Builder.Default
    int preparedStatementCacheSqlLimit = 2048;

    // 事务配置
    @Builder.Default
    boolean autoCommit = true;
    @Builder.Default
    String transactionIsolation = "TRANSACTION_READ_COMMITTED";
}