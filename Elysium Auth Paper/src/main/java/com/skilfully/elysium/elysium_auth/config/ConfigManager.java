package com.skilfully.elysium.elysium_auth.config;

import com.skilfully.elysium.elysium_auth.config.entity.StringList;
import com.skilfully.elysium.elysium_auth.data.GlobalData;
import com.skilfully.elysium.elysium_auth.utils.FileUtils;
import com.skilfully.elysium.elysium_auth.utils.MessageSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    public static YamlConfiguration plugin_config;
    public static YamlConfiguration plugin_language_config;
    public static Map<String, YamlConfiguration> language_configs;

    public static void loadConfig() throws IOException {
        language_configs = new HashMap<>();

        // 加载设置
        String setting_path = GlobalData.dataFolder + "setting.yml";
        File settingFile = new File(setting_path); // 获取文件 setting.yml
        if (!settingFile.exists()) {
            // setting.yml 不存在，创建文件夹 + 复制文件并且直接加载复制过去的配置文件
            if (!FileUtils.copyFileFromResources("files/setting.yml", setting_path, true)) {
                MessageSender.error("提取默认配置文件失败");
                throw new IOException("提取默认配置文件失败");
            }
            plugin_config = YamlConfiguration.loadConfiguration(new File(setting_path));
        } else {
            plugin_config = YamlConfiguration.loadConfiguration(settingFile);
        }


        // 加载语言
        StringList languages = new StringList(ConfigManager.plugin_config, "language"); // 读取 language
        File pluginLanguageConfigFile = new File(GlobalData.languageFolder + languages.value.get(0)); // 获取文件 xx-XX.yml
        if (!pluginLanguageConfigFile.exists()) {
            // 文件不存在，退级到 zh-CN.yml
            String zh_CN_path = GlobalData.languageFolder + "zh-CN.yml";
            File zh_CN = new File(zh_CN_path);
            if (!zh_CN.exists()) {
                // zh-CN.yml 也不存在，复制 + 加载
                if (!FileUtils.copyFileFromResources("languages/zh-CN.yml", zh_CN_path, true)) {
                    MessageSender.error("提取默认语言文件失败");
                    throw new IOException("提取默认语言文件失败");
                }
                plugin_language_config = YamlConfiguration.loadConfiguration(new File(zh_CN_path));
            } else {
                plugin_language_config = YamlConfiguration.loadConfiguration(zh_CN);
            }
        } else {
            plugin_language_config = YamlConfiguration.loadConfiguration(pluginLanguageConfigFile);
        }

        MessageSender.sendToConsole(plugin_language_config.getString("loadAsPluginLanguage"));
        List<String> languages_list = languages.value;
        if (languages_list.size() > 1) {
            for (int index = 1; index < languages_list.size(); index++) {
                String language = languages_list.get(index);
                language_configs.put(language, YamlConfiguration.loadConfiguration(new File(GlobalData.languageFolder + language)));
                MessageSender.sendToConsole("&e" + language + " &a已加载！");
            }
        }

    }

}
