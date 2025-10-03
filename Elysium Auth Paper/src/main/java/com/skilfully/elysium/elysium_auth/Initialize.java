package com.skilfully.elysium.elysium_auth;

import com.skilfully.elysium.elysium_auth.config.ConfigManager;
import com.skilfully.elysium.elysium_auth.data.GlobalData;
import com.skilfully.elysium.elysium_auth.database.DatabaseManager;
import com.skilfully.elysium.elysium_auth.database.entity.AccountData;
import com.skilfully.elysium.elysium_auth.utils.MessageSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.sqlite.core.DB;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;

public class Initialize {



    public static void init() throws IOException {
        // 创建文件夹
        Path workPath = Path.of(GlobalData.languageFolder);
        Files.createDirectories(workPath);
        // 加载配置文件&语言文件
        ConfigManager.loadConfig();
        // 加载数据库
        if (Objects.equals(ConfigManager.plugin_config.getString("authentication.type"), "local")) loadDatabase();


    }

    private static void loadDatabase() {
        YamlConfiguration config = ConfigManager.plugin_config;
        DatabaseManager dm = new DatabaseManager();
        String DBType = config.getString("authentication.local.database.type");
        if (DBType == null) throw new UnsupportedOperationException("不支持的数据库格式：null");
        switch (DBType) {
            case "MySQL" -> dm.initMySQL(
                    config.getString("authentication.local.database.MySQL.address"),
                    config.getString("authentication.local.database.MySQL.database"),
                    config.getString("authentication.local.database.MySQL.username"),
                    config.getString("authentication.local.database.MySQL.password"),
                    config.getBoolean("authentication.local.database.MySQL.ssl"));
            case "PostgreSQL" -> dm.initPostgreSQL(
                    config.getString("authentication.local.database.PostgreSQL.address"),
                    config.getString("authentication.local.database.PostgreSQL.database"),
                    config.getString("authentication.local.database.PostgreSQL.username"),
                    config.getString("authentication.local.database.PostgreSQL.password"),
                    config.getBoolean("authentication.local.database.PostgreSQL.ssl"));
            case "SQLite" -> dm.initSQLite(
                    config.getString("authentication.local.database.SQLite.address"));
            default -> throw new UnsupportedOperationException("不支持的数据库格式：" + DBType);
        }

        dm.saveAccountData(new AccountData()
                .setUuid("")
                .setBan(false)
                .setName("XiangYuanHuLian")
                .setPassword("123")
                .setOfflineLocationX(0.00)
                .setOfflineLocationY(0.00)
                .setOfflineLocationZ(0.00)
                .setOfflineLocationPitch(0.00)
                .setOfflineLocationYaw(0.00)
                .setRegisterDate(LocalDateTime.now())
        );

    }

}
