package com.skilfully.aetherfantasy.aetherfantasy_auth;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(
        id = "aetherfantasy_auth_velocity",
        name = "AetherFantasy Auth Velocity",
        version = BuildConstants.VERSION,
        description = "AetherFantasy Auth for Velocity",
        url = "Aatherfantasy.skilfully.cn",
        authors = {"AetherFantasy Studio"}
)
public class AetherFantasyAuthVelocity {

    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    }
}
