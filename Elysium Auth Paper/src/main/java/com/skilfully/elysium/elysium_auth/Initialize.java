package com.skilfully.elysium.elysium_auth;

import com.skilfully.elysium.elysium_auth.config.ConfigManager;
import com.skilfully.elysium.elysium_auth.data.GlobalData;
import com.skilfully.elysium.elysium_auth.database.DatabaseManager;
import com.skilfully.elysium.elysium_auth.utils.MessageSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Initialize {

    public static void init() throws IOException, UnsupportedOperationException, SQLException {
        // 创建文件夹
        Path workPath = Path.of(GlobalData.languageFolder);
        Files.createDirectories(workPath);
        // 加载配置文件&语言文件
        ConfigManager.loadConfig();
        // 禁用HikariCP日志并加载数据库
        if (Objects.equals(ConfigManager.plugin_config.getString("authentication.type"), "local")) {
            if (!loadDatabase()) {
                throw new RuntimeException("数据库加载失败");
            }
        }

        DatabaseManager.getInstance().executeDDL("""
                CREATE TABLE IF NOT EXISTS account (
                    uuid VARCHAR(255) NOT NULL PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    registerDate DATE NOT NULL,
                    mail VARCHAR(255),
                    mobile VARCHAR(255),
                    offlineLocationX DOUBLE NOT NULL,
                    offlineLocationY DOUBLE NOT NULL,
                    offlineLocationZ DOUBLE NOT NULL,
                    offlineLocationPitch DOUBLE NOT NULL,
                    offlineLocationYaw DOUBLE NOT NULL,
                    ban BOOLEAN NOT NULL DEFAULT 0,
                    banner VARCHAR(255),
                    banReason VARCHAR(255),
                    banTime DATE
                );
                """
        );

        int lows = DatabaseManager.getInstance().executeUpdate("INSERT INTO account(uuid, name, password, offlineLocationX, offlineLocationY, offlineLocationZ, offlineLocationPitch, offlineLocationYaw, registerDate) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                "123",
                "xbkz",
                "qwe",
                0.00,
                0.00,
                0.00,
                0.00,
                0.00,
                LocalDateTime.now()
        );

        MessageSender.sendToConsole(lows);


    }

    private static boolean loadDatabase() throws UnsupportedOperationException {
        YamlConfiguration config = ConfigManager.plugin_config;
        String DBType = config.getString("authentication.local.database.type");
        if (DBType == null) throw new UnsupportedOperationException("不支持的数据库格式：null");
        DatabaseManager dbManager = DatabaseManager.getInstance();
        switch (DBType) {
            case "MySQL" -> {
                Map<String, String> configMap = new HashMap<>();
                configMap.put("username", config.getString("authentication.local.database.MySQL.username"));
                configMap.put("password", config.getString("authentication.local.database.MySQL.password"));
                configMap.put("address", config.getString("authentication.local.database.MySQL.address"));
                configMap.put("ssl", config.getString("authentication.local.database.MySQL.ssl"));
                configMap.put("database", config.getString("authentication.local.database.MySQL.database"));
                return dbManager.initialize(DatabaseManager.DatabaseType.MYSQL, configMap);
            }
            case "PostgreSQL" -> {
                Map<String, String> configMap = new HashMap<>();
                configMap.put("username", config.getString("authentication.local.database.PostgreSQL.username"));
                configMap.put("password", config.getString("authentication.local.database.PostgreSQL.password"));
                configMap.put("address", config.getString("authentication.local.database.PostgreSQL.address"));
                configMap.put("ssl", config.getString("authentication.local.database.PostgreSQL.ssl"));
                configMap.put("database", config.getString("authentication.local.database.PostgreSQL.database"));
                return dbManager.initialize(DatabaseManager.DatabaseType.POSTGRESQL, configMap);
            }
            case "SQLite" -> {
                Map<String, String> configMap = Map.of(
                        "address", String.valueOf(config.getString("authentication.local.database.SQLite.address"))
                );
                return dbManager.initialize(DatabaseManager.DatabaseType.SQLITE, configMap);
            }
            default -> throw new UnsupportedOperationException("&c不支持的数据库格式：" + DBType);
        }

    }

}
