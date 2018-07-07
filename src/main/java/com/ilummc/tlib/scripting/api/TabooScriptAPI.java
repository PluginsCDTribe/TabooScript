package com.ilummc.tlib.scripting.api;

import com.ilummc.tlib.resources.TLocale;
import com.ilummc.tlib.scripting.TabooScript;
import com.ilummc.tlib.scripting.bukkit.GroovyPlugin;
import com.ilummc.tlib.scripting.bukkit.GroovyPluginLoader;
import me.skymc.taboolib.plugin.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TabooScriptAPI {

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

    public static void loadScripts() {
        for (File file : getScriptFiles()) {
            if (!GroovyPluginLoader.getPlugins().containsKey(file.getName().split("\\.")[0])) {
                try {
                    Bukkit.getPluginManager().loadPlugin(file);
                } catch (Exception e) {
                    TLocale.Logger.error("LOADING_SCRIPT_FAILED", file.getName(), e.toString());
                }
            }
        }
    }

    public static void enableScripts() {
        GroovyPluginLoader.getPlugins().values().stream().filter(plugin -> !plugin.isEnabled()).forEach(plugin -> Bukkit.getPluginManager().enablePlugin(plugin));
    }

    public static void disableScripts() {
        GroovyPluginLoader.getPlugins().values().forEach(PluginUtils::unload);
    }

    public static List<File> getScriptFiles() {
        return Arrays.stream(TabooScript.getInst().getScriptFolder().listFiles()).filter(TabooScriptAPI::isEffectiveFile).collect(Collectors.toList());
    }

    public static boolean isEffectiveFile(File file) {
        return file.getName().endsWith(".groovy") && !file.getName().startsWith("-");
    }
}
