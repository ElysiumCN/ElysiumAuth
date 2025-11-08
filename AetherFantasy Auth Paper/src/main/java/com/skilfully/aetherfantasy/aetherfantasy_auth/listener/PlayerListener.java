package com.skilfully.aetherfantasy.aetherfantasy_auth.listener;

import com.skilfully.aetherfantasy.aetherfantasy_auth.data.GlobalData;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {

    public static String kick_server_messageContent_server_loading;
    public static String kick_server_messageContent_invalid_name;
    public static String player_name_regex;

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (GlobalData.isServerLoading) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.kickMessage(LegacyComponentSerializer.legacySection().deserialize(kick_server_messageContent_server_loading));
            return;
        }

        if (!event.getName().matches(player_name_regex)) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.kickMessage(LegacyComponentSerializer.legacySection().deserialize(kick_server_messageContent_invalid_name));
            return;
        }

    }

}
