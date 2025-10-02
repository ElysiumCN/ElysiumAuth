package com.skilfully.elysium.elysium_auth.database.config;

import lombok.Getter;

/**
 * 数据库类型枚举
 *
 * @since 0.1
 *
 * @author 西博卡兹
 */
@Getter
public enum DatabaseType {

    MYSQL("MySQL"),
    SQLITE("SQLite"),
    POSTGRESQL("PostgreSQL");

    private final String value;

    DatabaseType(String value) {
        this.value = value;
    }

    public static DatabaseType fromString(String value) {
        for (DatabaseType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的数据库类型: " + value);
    }
}
