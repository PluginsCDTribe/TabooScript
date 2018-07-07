package com.ilummc.tlib.scripting.api;

import com.ilummc.tlib.scripting.bukkit.GroovyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class TabooScriptApi {

    public static void registerProperty(String name, Class<?> clazz) {
        Arrays.stream(Bukkit.getPluginManager().getPlugins()).filter(plugin -> plugin instanceof GroovyPlugin)
                .map(plugin -> ((GroovyPlugin) plugin)).forEach(plugin -> {
            try {
                plugin.addProperty(name, clazz.getConstructor(Plugin.class).newInstance(plugin));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
            }
        });
    }

}
