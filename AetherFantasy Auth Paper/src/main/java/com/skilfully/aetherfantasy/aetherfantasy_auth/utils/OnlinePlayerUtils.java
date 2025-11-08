package com.skilfully.aetherfantasy.aetherfantasy_auth.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 在线玩家工具类
 */
public class OnlinePlayerUtils {

    /**
     * 获取所有在线玩家
     */
    public static Collection<? extends Player> getAllOnlinePlayers() {
        return Bukkit.getOnlinePlayers();
    }

    /**
     * 获取所有在线玩家的名称列表
     */
    public static List<String> getOnlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    /**
     * 获取在线玩家数量
     */
    public static int getOnlinePlayerCount() {
        return Bukkit.getOnlinePlayers().size();
    }

    /**
     * 根据名称查找在线玩家
     */
    public static Player getOnlinePlayer(String playerName) {
        return Bukkit.getPlayer(playerName);
    }

    /**
     * 根据名称查找在线玩家（忽略大小写）
     */
    public static Player getOnlinePlayerIgnoreCase(String playerName) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(playerName)) {
                return player;
            }
        }
        return null;
    }

    /**
     * 检查玩家是否在线
     */
    public static boolean isPlayerOnline(String playerName) {
        return Bukkit.getPlayer(playerName) != null;
    }

    /**
     * 获取有特定权限的在线玩家
     */
    public static List<Player> getOnlinePlayersWithPermission(String permission) {
        List<Player> playersWithPermission = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(permission)) {
                playersWithPermission.add(player);
            }
        }
        return playersWithPermission;
    }

    /**
     * 获取在特定世界的在线玩家
     */
    public static List<Player> getOnlinePlayersInWorld(String worldName) {
        List<Player> playersInWorld = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().getName().equals(worldName)) {
                playersInWorld.add(player);
            }
        }
        return playersInWorld;
    }

    /**
     * 获取OP玩家列表
     */
    public static List<Player> getOnlineOpPlayers() {
        List<Player> opPlayers = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) {
                opPlayers.add(player);
            }
        }
        return opPlayers;
    }
}