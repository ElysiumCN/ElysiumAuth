package com.skilfully.elysium.elysium_auth;

import com.skilfully.elysium.elysium_auth.utils.MessageSender;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.SQLException;

public final class ElysiumAuthPaper extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            Initialize.init();
        } catch (RuntimeException | IOException | SQLException e) {
            if (e.getMessage() != null) {
                MessageSender.sendToConsole("&c" + e.getMessage());
                MessageSender.error("", e);
            }
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
