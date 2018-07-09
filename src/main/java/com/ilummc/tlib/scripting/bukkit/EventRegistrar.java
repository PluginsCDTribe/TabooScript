package com.ilummc.tlib.scripting.bukkit;

import com.ilummc.tlib.scripting.TabooScript;
import com.ilummc.tlib.scripting.monitor.PluginMonitor;
import groovy.lang.Closure;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.IllegalPluginAccessException;

import java.util.Optional;

public class EventRegistrar {

    public static Optional<Class<?>> findEvent(String name) {
        for (String s : TabooScript.getConf().getStringList("eventPackages")) {
            try {
                return Optional.ofNullable(Class.forName(s + "." + name));
            } catch (ClassNotFoundException ignored) {
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public static void register(Class<?> clazz, EventPriority priority, boolean ignoreCancelled, Closure closure, GroovyPlugin plugin) {
        if (isSubClass(clazz, Event.class)) {
            try {
                Bukkit.getPluginManager().registerEvents(SingleListener.of(((Class<? extends Event>) clazz), priority, ignoreCancelled, plugin, closure), plugin);
            } catch (IllegalPluginAccessException e) {
                if (e.getMessage().contains("Plugin attempted to register") || e.getMessage().contains("while not enabled")) {
                    PluginMonitor.printEventRegisterError(plugin, e, clazz.getSimpleName(), "Plugin attempted to register listener while not enabled");
                } else {
                    throw new IllegalPluginAccessException(e.getMessage());
                }
            }
        }
    }

    public static boolean isSubClass(Class<?> sub, Class<?> superClass) {
        try {
            sub.asSubclass(superClass);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
