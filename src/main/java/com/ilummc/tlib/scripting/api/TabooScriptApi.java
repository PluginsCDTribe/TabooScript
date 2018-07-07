package com.ilummc.tlib.scripting.api;

import com.ilummc.tlib.scripting.bukkit.GroovyPluginLoader;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;

public class TabooScriptApi {

    public static void registerProperty(String name, Class<?> clazz) {
        GroovyPluginLoader.getPlugins().values()
                .forEach(plugin -> {
                    try {
                        plugin.addProperty(name, clazz.getConstructor(Plugin.class).newInstance(plugin));
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                });
    }

}
