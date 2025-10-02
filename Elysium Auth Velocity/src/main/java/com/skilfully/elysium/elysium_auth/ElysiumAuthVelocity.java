package com.skilfully.elysium.elysium_auth;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(
        id = "elysium_auth_velocity",
        name = "Elysium Auth Velocity",
        version = BuildConstants.VERSION,
        description = "Elysium Auth for Velocity",
        url = "elysium.skilfully.cn",
        authors = {"Elysium Studio"}
)
public class ElysiumAuthVelocity {

    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    }
}
