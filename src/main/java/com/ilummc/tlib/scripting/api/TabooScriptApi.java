package com.ilummc.tlib.scripting.api;

import com.ilummc.tlib.scripting.bukkit.GroovyPlugin;
import com.ilummc.tlib.scripting.bukkit.GroovyPluginLoader;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class TabooScriptApi {

    private static Map<String, Class<?>> registeredProperties = new HashMap<>();

    public static void registerProperty(String name, Class<?> clazz) {
        registeredProperties.put(name, clazz);
        GroovyPluginLoader.getPlugins().values()
                .forEach(plugin -> {
                    try {
                        plugin.addProperty(name, clazz.getConstructor(Plugin.class).newInstance(plugin));
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                });
    }

    public static void addProperties(GroovyPlugin plugin) {
        registeredProperties.forEach((name, clazz) -> {
            try {
                plugin.addProperty(name, clazz.getConstructor(Plugin.class).newInstance(plugin));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        });
    }

}
