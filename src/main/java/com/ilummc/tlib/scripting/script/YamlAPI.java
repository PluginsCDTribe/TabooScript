package com.ilummc.tlib.scripting.script;

import com.ilummc.tlib.scripting.TabooScript;
import me.skymc.taboolib.fileutils.FileUtils;
import me.skymc.taboolib.playerdata.DataUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

/**
 * @Author sky
 * @Since 2018-07-10 3:43
 */
public class YamlAPI {

    private final Plugin plugin;

    public YamlAPI(Plugin plugin) {
        this.plugin = plugin;
    }

    public YamlConfiguration create(File file) {
        return YamlConfiguration.loadConfiguration(FileUtils.createNewFile(checkParentFile(file)));
    }

    public YamlConfiguration create(String filePath) {
        return YamlConfiguration.loadConfiguration(FileUtils.createNewFile(checkParentFile(new File(filePath))));
    }

    public YamlConfiguration create(String name, Plugin plugin) {
        return YamlConfiguration.loadConfiguration(FileUtils.file(getPluginFolder(plugin), name));
    }

    public void save(FileConfiguration conf, String filePath) {
        DataUtils.saveConfiguration(conf, FileUtils.createNewFile(checkParentFile(new File(filePath))));
    }

    public void save(FileConfiguration conf, File file) {
        DataUtils.saveConfiguration(conf, FileUtils.createNewFile(checkParentFile(file)));
    }

    public void save(FileConfiguration conf, String name, Plugin plugin) {
        DataUtils.saveConfiguration(conf, FileUtils.file(getPluginFolder(plugin), name));
    }

    File checkParentFile(File file) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return file;
    }

    File getPluginFolder(Plugin plugin) {
        if (plugin == null || plugin.equals(TabooScript.getInst())) {
            return TabooScript.getInst().getScriptFolder();
        } else {
            File file = new File("plugins/" + plugin.getName());
            file.mkdirs();
            return file;
        }
    }
}
