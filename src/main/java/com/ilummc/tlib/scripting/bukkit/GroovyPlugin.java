package com.ilummc.tlib.scripting.bukkit;

import com.ilummc.tlib.scripting.TabooScriptMain;
import com.ilummc.tlib.scripting.script.GroovyPluginApi;
import groovy.lang.GroovyObject;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginBase;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

public class GroovyPlugin extends PluginBase implements Plugin {

    private boolean enabled = false;

    private GroovyPluginLoader loader;
    private GroovyObject groovyObject;

    private PluginDescriptionFile description;

    public GroovyPlugin(GroovyPluginLoader loader, GroovyObject groovyObject) {
        this.loader = loader;
        this.groovyObject = groovyObject;
    }

    private GroovyPluginApi api;

    @Override
    public File getDataFolder() {
        return TabooScriptMain.instance().getConf().getDataFolder(this);
    }

    @Override
    public PluginDescriptionFile getDescription() {
        return description;
    }

    @Override
    public FileConfiguration getConfig() {
        return null;
    }

    @Override
    public InputStream getResource(String filename) {
        return null;
    }

    @Override
    public void saveConfig() {

    }

    @Override
    public void saveDefaultConfig() {

    }

    @Override
    public void saveResource(String resourcePath, boolean replace) {

    }

    @Override
    public void reloadConfig() {

    }

    @Override
    public PluginLoader getPluginLoader() {
        return loader;
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void onDisable() {
        enabled = false;
        api.getOnDisable().call();
    }

    @Override
    public void onLoad() {
        api.getOnLoad().call();
    }

    @Override
    public void onEnable() {
        enabled = true;
        try {
            api.getOnEnable().call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isNaggable() {
        return false;
    }

    @Override
    public void setNaggable(boolean canNag) {

    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return null;
    }

    @Override
    public Logger getLogger() {
        return Bukkit.getLogger();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    public GroovyPluginApi getApi() {
        return api;
    }

    void setApi(GroovyPluginApi api) {
        this.api = api;
    }

    void setDescription(PluginDescriptionFile description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return getDescription().getFullName();
    }

    public static Plugin getProviderPlugin(Class<?> clazz) {
        if (clazz.getClassLoader() instanceof GroovyPluginClassLoader) {
            return ((GroovyPluginClassLoader) clazz.getClassLoader()).plugin;
        } else return JavaPlugin.getPlugin(TabooScriptMain.class);
    }

    GroovyObject getGroovyObject() {
        return groovyObject;
    }

    public void addProperty(String name, Object object) {
        groovyObject.setProperty(name, object);
        api.setProperty(name, object);
    }

}
