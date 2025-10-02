package com.skilfully.elysium.elysium_auth.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

/**
 * 消息发送工具类
 *
 * <p>提供向控制台、玩家和命令发送者发送消息的统一接口，支持颜色代码和占位符替换</p>
 * <p>sendToConsole 系列方法用于用户可见的彩色消息，info/warn/error 系列方法用于系统日志记录</p>
 *
 * @author skilfully
 * @version 1.2
 * @see ChatColor
 * @see CommandSender
 */
public class MessageSender {

    public static final String CONSOLE_MESSAGE_PREFIX = "&7| &3Elysium&eAuth &7| &r";
    public static String PLAYER_MESSAGE_PREFIX = "";

    /**
     * 发送消息到指定目标
     *
     * @param message 消息内容，支持任意对象类型
     * @param target 消息接收目标
     */
    private static void sendMessage(Object message, CommandSender target) {
        String rawMessage = String.valueOf(message);
        String formattedMessage;

        if (target instanceof ConsoleCommandSender) {
            // 控制台消息：添加前缀并转换颜色代码
            formattedMessage = CONSOLE_MESSAGE_PREFIX + rawMessage;
        } else {
            // 玩家消息：添加前缀并转换颜色代码
            formattedMessage = PLAYER_MESSAGE_PREFIX + rawMessage;
        }
        formattedMessage = ChatColor.translateAlternateColorCodes('&', formattedMessage);

        target.sendMessage(formattedMessage);
    }

    /**
     * 发送消息到指定玩家
     *
     * @param message 消息内容，支持任意对象类型
     * @param player 玩家对象
     */
    private static void sendMessage(Object message, Player player) {
        sendMessage(message, (CommandSender) player);
    }

    /**
     * 向控制台发送彩色消息（用于用户可见的消息）
     *
     * @param message 消息内容，支持任意对象类型
     */
    public static void sendToConsole(Object message) {
        sendMessage(message, Bukkit.getConsoleSender());
    }

    /**
     * 向控制台发送格式化彩色消息（用于用户可见的消息）
     *
     * @param message 消息模板，支持 {} 占位符
     * @param placeholders 占位符替换值，支持任意对象类型
     */
    public static void sendToConsole(Object message, Object... placeholders) {
        String formattedMessage = formatMessage(String.valueOf(message), placeholders);
        sendMessage(formattedMessage, Bukkit.getConsoleSender());
    }

    /**
     * 向玩家发送消息
     *
     * @param player 目标玩家
     * @param message 消息内容，支持任意对象类型
     */
    public static void sendToPlayer(Player player, Object message) {
        sendMessage(message, player);
    }

    /**
     * 向玩家发送格式化消息
     *
     * @param player 目标玩家
     * @param message 消息模板，支持 {} 占位符
     * @param placeholders 占位符替换值，支持任意对象类型
     */
    public static void sendToPlayer(Player player, Object message, Object... placeholders) {
        String formattedMessage = formatMessage(String.valueOf(message), placeholders);
        sendMessage(formattedMessage, player);
    }

    /**
     * 向命令发送者发送消息
     *
     * @param sender 命令发送者
     * @param message 消息内容，支持任意对象类型
     */
    public static void sendToSender(CommandSender sender, Object message) {
        sendMessage(message, sender);
    }

    /**
     * 向命令发送者发送格式化消息
     *
     * @param sender 命令发送者
     * @param message 消息模板，支持 {} 占位符
     * @param placeholders 占位符替换值，支持任意对象类型
     */
    public static void sendToSender(CommandSender sender, Object message, Object... placeholders) {
        String formattedMessage = formatMessage(String.valueOf(message), placeholders);
        sendMessage(formattedMessage, sender);
    }

    /**
     * 记录信息日志到控制台（用于系统日志，带时间戳）
     *
     * @param message 信息消息内容
     */
    public static void info(Object message) {
        Bukkit.getLogger().info(String.valueOf(message));
    }

    /**
     * 记录带占位符的信息日志到控制台（用于系统日志，带时间戳）
     *
     * @param message 信息消息模板，支持 {} 占位符
     * @param placeholders 占位符替换值
     */
    public static void info(Object message, Object... placeholders) {
        String formattedMessage = formatMessage(String.valueOf(message), placeholders);
        Bukkit.getLogger().info(formattedMessage);
    }

    /**
     * 记录警告日志到控制台（用于系统日志，带时间戳）
     *
     * @param message 警告消息内容
     */
    public static void warn(Object message) {
        Bukkit.getLogger().warning(String.valueOf(message));
    }

    /**
     * 记录带占位符的警告日志到控制台（用于系统日志，带时间戳）
     *
     * @param message 警告消息模板，支持 {} 占位符
     * @param placeholders 占位符替换值
     */
    public static void warn(Object message, Object... placeholders) {
        String formattedMessage = formatMessage(String.valueOf(message), placeholders);
        Bukkit.getLogger().warning(formattedMessage);
    }

    /**
     * 记录错误日志到控制台（用于系统日志，带时间戳）
     *
     * @param message 错误消息内容
     */
    public static void error(Object message) {
        Bukkit.getLogger().severe(String.valueOf(message));
    }

    /**
     * 记录带占位符的错误日志到控制台（用于系统日志，带时间戳）
     *
     * @param message 错误消息模板，支持 {} 占位符
     * @param placeholders 占位符替换值
     */
    public static void error(Object message, Object... placeholders) {
        String formattedMessage = formatMessage(String.valueOf(message), placeholders);
        Bukkit.getLogger().severe(formattedMessage);
    }

    /**
     * 记录带异常的错误日志到控制台（用于系统日志，带时间戳和堆栈跟踪）
     *
     * @param message 错误消息内容
     * @param throwable 异常对象
     */
    public static void error(Object message, Throwable throwable) {
        Bukkit.getLogger().log(Level.SEVERE, String.valueOf(message), throwable);
    }

    /**
     * 记录带占位符和异常的错误日志到控制台（用于系统日志，带时间戳和堆栈跟踪）
     *
     * @param message 错误消息模板，支持 {} 占位符
     * @param throwable 异常对象
     * @param placeholders 占位符替换值
     */
    public static void error(Object message, Throwable throwable, Object... placeholders) {
        String formattedMessage = formatMessage(String.valueOf(message), placeholders);
        Bukkit.getLogger().log(Level.SEVERE, formattedMessage, throwable);
    }

    /**
     * 格式化消息，替换 {} 占位符
     *
     * @param message 原始消息模板
     * @param placeholders 占位符替换值数组
     * @return 格式化后的消息
     */
    private static String formatMessage(String message, Object... placeholders) {
        if (placeholders == null || placeholders.length == 0) {
            return message;
        }

        StringBuilder result = new StringBuilder();
        int placeholderIndex = 0;
        int lastIndex = 0;
        int currentIndex = 0;

        while (currentIndex < message.length() - 1) {
            if (message.charAt(currentIndex) == '{' && message.charAt(currentIndex + 1) == '}') {
                // 添加占位符前的文本
                result.append(message, lastIndex, currentIndex);

                // 替换占位符
                if (placeholderIndex < placeholders.length) {
                    result.append(placeholders[placeholderIndex]);
                    placeholderIndex++;
                } else {
                    // 占位符数量不足，保留原占位符
                    result.append("{}");
                }

                // 跳过占位符
                currentIndex += 2;
                lastIndex = currentIndex;
            } else {
                currentIndex++;
            }
        }

        // 添加剩余文本
        result.append(message, lastIndex, message.length());

        return result.toString();
    }

    /**
     * 设置玩家消息前缀
     *
     * @param prefix 前缀字符串，支持颜色代码
     */
    public static void setPlayerMessagePrefix(String prefix) {
        PLAYER_MESSAGE_PREFIX = prefix;
    }
}