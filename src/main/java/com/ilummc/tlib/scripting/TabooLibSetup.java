package com.ilummc.tlib.scripting;

import me.skymc.taboolib.TabooLib;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.NumberConversions;

/**
 * @Author sky
 * @Since 2018-09-25 23:42
 */
public class TabooLibSetup {

    public static boolean checkVersion(Plugin plugin, double requiredVersion) {
        if (NumberConversions.toDouble(TabooLib.instance().getDescription().getVersion()) < requiredVersion) {
            Bukkit.getConsoleSender().sendMessage("§8[§4§l" + plugin.getName() + "§8] §c" + "");
            Bukkit.getConsoleSender().sendMessage("§8[§4§l" + plugin.getName() + "§8] §c" + "#################### 错误 ####################");
            Bukkit.getConsoleSender().sendMessage("§8[§4§l" + plugin.getName() + "§8] §c" + "");
            Bukkit.getConsoleSender().sendMessage("§8[§4§l" + plugin.getName() + "§8] §c" + "  插件无法正常启动!");
            Bukkit.getConsoleSender().sendMessage("§8[§4§l" + plugin.getName() + "§8] §c" + "  因为您的 §4TabooLib §c插件版本版本过低.");
            Bukkit.getConsoleSender().sendMessage("§8[§4§l" + plugin.getName() + "§8] §c" + "");
            Bukkit.getConsoleSender().sendMessage("§8[§4§l" + plugin.getName() + "§8] §c" + "  插件最低要求版本为 §4v" + requiredVersion);
            Bukkit.getConsoleSender().sendMessage("§8[§4§l" + plugin.getName() + "§8] §c" + "");
            Bukkit.getConsoleSender().sendMessage("§8[§4§l" + plugin.getName() + "§8] §c" + "  下载地址:");
            Bukkit.getConsoleSender().sendMessage("§8[§4§l" + plugin.getName() + "§8] §c" + "  §4https://github.com/Bkm016/TabooLib/releases");
            Bukkit.getConsoleSender().sendMessage("§8[§4§l" + plugin.getName() + "§8] §c" + "");
            Bukkit.getConsoleSender().sendMessage("§8[§4§l" + plugin.getName() + "§8] §c" + "#################### 错误 ####################");
            Bukkit.getConsoleSender().sendMessage("§8[§4§l" + plugin.getName() + "§8] §c" + "");
            try {
                Thread.sleep(20000);
            } catch (Exception ignored) {
            }
            return false;
        }
        return true;
    }

}
