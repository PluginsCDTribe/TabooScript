package com.ilummc.tlib.scripting.script;

import me.skymc.taboolib.display.ActionUtils;
import me.skymc.taboolib.display.TitleUtils;
import me.skymc.taboolib.itagapi.TagDataHandler;
import me.skymc.taboolib.json.tellraw.TellrawJson;
import me.skymc.taboolib.particle.EffLib;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TabooLibAPI {

    private final Plugin plugin;

    public TabooLibAPI(Plugin plugin) {
        this.plugin = plugin;
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

    public void actionbar(Player player, String text) {
        ActionUtils.send(player, text);
    }

    public EffLib effectLib(String effect) {
        return EffLib.fromName(effect);
    }

    public TellrawJson createJson() {
        return TellrawJson.create();
    }

    public TagDataHandler tagHandler() {
        return TagDataHandler.getHandler();
    }
}
