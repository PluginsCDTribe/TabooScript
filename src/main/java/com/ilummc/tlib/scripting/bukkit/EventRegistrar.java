package com.ilummc.tlib.scripting.bukkit;

import com.ilummc.tlib.scripting.TabooScript;
import groovy.lang.Closure;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

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
    public static void register(Class<?> clazz, EventPriority priority, boolean ignoreCancelled, Closure closure, Plugin plugin) {
        if (isSubClass(clazz, Event.class)) {
            Bukkit.getPluginManager().registerEvents(SingleListener.of(((Class<? extends Event>) clazz), priority, ignoreCancelled, closure), plugin);
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
