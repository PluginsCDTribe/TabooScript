package com.ilummc.tlib.scripting.tlib;

import me.skymc.taboolib.display.ActionUtils;
import me.skymc.taboolib.display.TitleUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TabooLibApi {

    private final Plugin plugin;

    public TabooLibApi(Plugin plugin) {
        this.plugin = plugin;
    }

    public void actionbar(Player player, String text) {
        ActionUtils.send(player, text);
    }

    public void title(Player player, String main) {
        title(player, main, "");
    }

    public void title(Player player, String main, String sub) {
        title(player, main, sub, 10, 5, 10);
    }

    public void title(Player player, String main, String sub, int fadeIn, int stay, int fadeOut) {
        TitleUtils.sendTitle(player, main, sub, fadeIn, stay, fadeOut);
    }

}
