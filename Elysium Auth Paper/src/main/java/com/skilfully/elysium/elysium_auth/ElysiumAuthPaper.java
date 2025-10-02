package com.skilfully.elysium.elysium_auth;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class ElysiumAuthPaper extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            Initialize.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
