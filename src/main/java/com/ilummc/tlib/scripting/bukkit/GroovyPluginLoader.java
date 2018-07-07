package com.ilummc.tlib.scripting.bukkit;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.ilummc.tlib.resources.TLocale;
import com.ilummc.tlib.scripting.scriptapi.GroovyPluginApi;
import com.ilummc.tlib.scripting.scriptapi.InternalApi;
import com.ilummc.tlib.scripting.util.Entries;
import groovy.lang.GroovyObject;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class GroovyPluginLoader implements PluginLoader {

    private static final Pattern[] PATTERNS = {Pattern.compile("[^\\-]*\\.groovy$")};

    private static final Map<String, GroovyPlugin> pluginMap = new HashMap<>();

    private final Map<File, GroovyPlugin> fileMap = new HashMap<>();

    private final Server server;

    public GroovyPluginLoader(Server server) {
        this.server = server;
    }

    @Override
    public Plugin loadPlugin(File file) throws InvalidPluginException, UnknownDependencyException {
        if (fileMap.containsKey(file)) {
            GroovyPlugin plugin = fileMap.get(file);
            if (plugin == null) throw new InvalidPluginException("Invalid plugin: loading failed.");
            return setup(plugin);
        } else return setup(loadFromFile(file).getValue());
    }

    @Override
    public PluginDescriptionFile getPluginDescription(File file) throws InvalidDescriptionException {
        if (!fileMap.containsKey(file)) return loadFromFile(file).getKey();
        else if (fileMap.get(file) != null) return fileMap.get(file).getDescription();
        else throw new InvalidDescriptionException("Invalid description: loading failed.");
    }

    private Plugin setup(GroovyPlugin plugin) {
        plugin.onLoad();
        return plugin;
    }

    private Map.Entry<PluginDescriptionFile, GroovyPlugin> loadFromFile(File file) throws RuntimeException {
        try {
            String s = Files.toString(file, StandardCharsets.UTF_8);
            GroovyPluginClassLoader loader = new GroovyPluginClassLoader(file.getName());
            GroovyPluginClassLoader.LOADERS.add(loader);
            Class clazz = loader.parseClass(s);
            Object o = clazz.newInstance();
            if (o instanceof GroovyObject) {
                GroovyPlugin plugin = new GroovyPlugin(this, ((GroovyObject) o));
                GroovyPluginApi api = new GroovyPluginApi(plugin);
                plugin.setApi(api);
                ((GroovyObject) o).setProperty("bukkit", Bukkit.getServer());
                ((GroovyObject) o).setProperty("api", InternalApi.INSTANCE);
                ((GroovyObject) o).setProperty("plugin", api);
                ((GroovyObject) o).invokeMethod("run", new Object[0]);
                PluginDescriptionFile descriptionFile = api.toDescription(o);
                pluginMap.put(descriptionFile.getName(), plugin);
                fileMap.put(file, plugin);
                loader.setPlugin(plugin);
                plugin.setDescription(descriptionFile);
                return Entries.of(descriptionFile, plugin);
            } else {
                TLocale.Logger.warn("NOT_VALID_SCRIPT", file.getName());
                loader.removeClasses();
                throw new RuntimeException("Not a groovy script but a class.");
            }
        } catch (IOException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        } finally {
            fileMap.putIfAbsent(file, null);
        }
    }

    @Override
    public Pattern[] getPluginFileFilters() {
        return PATTERNS;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(Listener listener, Plugin plugin) {
        if (listener instanceof SingleListener) {
            return ImmutableMap.of(((SingleListener) listener).getType(),
                    ImmutableSet.of(new RegisteredListener(listener, ((SingleListener) listener),
                            ((SingleListener) listener).getPriority(), plugin, ((SingleListener) listener).ignoreCancelled())));
        } else {
            return ImmutableMap.of();
        }
    }

    @Override
    public void enablePlugin(Plugin plugin) {
        try {
            if (!plugin.isEnabled()) {
                TLocale.Logger.info("ENABLE_SCRIPT", plugin.toString());
                plugin.onEnable();
            }
        } catch (Throwable e) {
            TLocale.Logger.error("ENABLE_ERROR", plugin.getName(), e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void disablePlugin(Plugin plugin) {
        try {
            if (plugin.isEnabled()) {
                TLocale.Logger.info("DISABLE_SCRIPT", plugin.toString());
                plugin.onDisable();
                GroovyPluginClassLoader.LOADERS.stream().filter(loader -> loader.plugin == plugin)
                        .forEach(GroovyPluginClassLoader::removeClasses);
            }
        } catch (Throwable e) {
            TLocale.Logger.error("DISABLE_ERROR", plugin.getName(), e.toString());
            e.printStackTrace();
        }
    }

    public static Map<String, GroovyPlugin> getPlugins() {
        return pluginMap;
    }

}
