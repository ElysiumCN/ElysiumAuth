package com.skilfully.aetherfantasy.aetherfantasy_auth.utils;

import com.skilfully.aetherfantasy.aetherfantasy_auth.AetherFantasyAuthPaper;
import com.skilfully.aetherfantasy.aetherfantasy_auth.data.GlobalData;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class FileUtils {

    /**
     * 从 jar resources 目录复制文件到指定目录
     * @param resourcePath 资源文件目录
     * @param targetPath 目标目录
     * @param overwrite 覆盖
     * @return 复制成功
     * @throws IOException 复制失败：IO异常
     */
    public static boolean copyFileFromResources(String resourcePath, @Nullable String targetPath, boolean overwrite) throws IOException {
        JavaPlugin plugin = AetherFantasyAuthPaper.getPlugin(AetherFantasyAuthPaper.class);
        if (targetPath == null) targetPath = GlobalData.dataFolder;
        // 处理目标路径：如果是相对路径，则基于插件的dataFolder
        File targetFile = new File(targetPath);

        // 如果文件已存在且不允许覆盖，直接返回
        if (targetFile.exists() && !overwrite) {
            return true;
        }

        // 确保目标目录存在（自动创建多级目录）
        File parentDir = targetFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                return false;
            }
        }

        // 复制文件内容
        try (InputStream in = plugin.getResource(resourcePath);
             OutputStream out = Files.newOutputStream(targetFile.toPath())) {

            if (in == null) {
                return false;
            }

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            return true;
        }
    }

}
