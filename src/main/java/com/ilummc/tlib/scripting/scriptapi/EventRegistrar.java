package com.ilummc.tlib.scripting.scriptapi;

import com.google.common.collect.ImmutableList;
import com.ilummc.tlib.resources.TLocale;
import groovy.lang.Closure;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Optional;

public class EventRegistrar {

    private static List<String> packages = ImmutableList.of("org.bukkit.event.enchantment.", "org.bukkit.event.server.",
            "org.bukkit.event.entity.", "org.bukkit.event.player.", "org.bukkit.event.block.",
            "org.bukkit.event.hanging.", "org.bukkit.event.inventory.", "org.bukkit.event.vehicle.", "org.bukkit.event.weather.",
            "org.bukkit.event.world.", "org.spigotmc.event.player.", "org.spigotmc.event.entity.",
            "me.skymc.taboolib.events.", "me.skymc.taboolib.events.itag.");

    static Optional<Class<?>> findEvent(String name) {
        for (String s : packages) {
            try {
                return Optional.ofNullable(Class.forName(s + name));
            } catch (ClassNotFoundException ignored) {
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    static void register(Class<?> clazz, EventPriority priority, boolean ignoreCancelled, Closure closure, Plugin plugin) {
        if (isSubClass(clazz, Event.class)) {
            TLocale.Logger.fine("EVENT_REGISTER", plugin.toString(), clazz.getSimpleName());
            Bukkit.getPluginManager().registerEvents(SingleListener.of(((Class<? extends Event>) clazz), priority, ignoreCancelled, closure),
                    plugin);
        }
    }

    private static boolean isSubClass(Class<?> sub, Class<?> superClass) {
        try {
            sub.asSubclass(superClass);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
