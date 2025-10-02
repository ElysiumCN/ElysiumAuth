package com.skilfully.elysium.elysium_auth;

import com.skilfully.elysium.elysium_auth.config.ConfigManager;
import com.skilfully.elysium.elysium_auth.data.GlobalData;
import com.skilfully.elysium.elysium_auth.database.DatabaseManager;
import com.skilfully.elysium.elysium_auth.database.dao.AccountDataDAO;
import com.skilfully.elysium.elysium_auth.database.model.AccountData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private static void loadDatabase() throws IOException {
        String sqlType = ConfigManager.plugin_config.getString("authentication.local.database.type");
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        databaseManager.initWithCache(
                sqlType,
                ConfigManager.plugin_config.getString("authentication.local.database." + sqlType + ".address"),
                ConfigManager.plugin_config.getString("authentication.local.database." + sqlType + ".username"),
                ConfigManager.plugin_config.getString("authentication.local.database." + sqlType + ".password")
        );

        AccountDataDAO userDao = databaseManager.getAccountDataDAO();
        userDao.insert(AccountData.builder()
                .name("123")
                .uuid("123")
                .password("123")
                .build());

        // 关闭连接
        Runtime.getRuntime().addShutdownHook(new Thread(databaseManager::close));

    }

}
