package com.ilummc.tlib.scripting;

import com.ilummc.tlib.annotations.Dependency;
import com.ilummc.tlib.inject.TDependencyInjector;
import com.ilummc.tlib.logger.TLogger;
import com.ilummc.tlib.resources.TLocale;
import com.ilummc.tlib.scripting.api.TabooScriptAPI;
import com.ilummc.tlib.scripting.bstats.Metrics;
import com.ilummc.tlib.scripting.bukkit.GroovyPluginLoader;
import com.ilummc.tlib.scripting.script.*;
import me.skymc.taboolib.fileutils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Dependency(type = Dependency.Type.LIBRARY, maven = "org.codehaus.groovy:groovy:2.5.0")
@Dependency(type = Dependency.Type.LIBRARY, maven = "org.codehaus.groovy:groovy-bsf:2.5.0")
@Dependency(type = Dependency.Type.LIBRARY, maven = "org.ow2.asm:asm-commons:6.2")
public class TabooScript extends JavaPlugin {

    private static TabooScript ins;
    private static FileConfiguration config;
    private TLogger logger;
    private File scriptFolder;

    @Override
    public void onLoad() {
        ins = this;
        logger = TLogger.getUnformatted(this);

        if (!TabooLibSetup.checkVersion(this, 4.6)) {
            return;
        }

        reloadConfig();
        createFolder();
        registerProperty();

        TDependencyInjector.inject(this, this);
        Bukkit.getPluginManager().registerInterface(GroovyPluginLoader.class);

        TLocale.Logger.info("LOADING_SCRIPTS");
        TabooScriptAPI.loadScripts();
    }

    @Override
    public void onEnable() {
        new Metrics(this);
        Bukkit.getScheduler().runTask(this, TabooScriptAPI::enableScripts);
    }

    @Override
    public void onDisable() {
        TabooScriptAPI.disableScripts();
    }

    @Override
    public void reloadConfig() {
        config = ConfigUtils.saveDefaultConfig(this, "config.yml");
    }

    private void registerProperty() {
        // TabooLib
        TabooScriptAPI.registerProperty("lib", TabooLibAPI.class);
        TabooScriptAPI.registerProperty("tlib", TabooLibAPI.class);
        TabooScriptAPI.registerProperty("taboolib", TabooLibAPI.class);
        // Bukkit
        TabooScriptAPI.registerProperty("yaml", YamlAPI.class);
        TabooScriptAPI.registerProperty("sound", SoundAPI.class);
        TabooScriptAPI.registerProperty("number", NumberAPI.class);
        TabooScriptAPI.registerProperty("entity", EntityAPI.class);
        TabooScriptAPI.registerProperty("location", LocationAPI.class);
    }

    private void createFolder() {
        scriptFolder = new File(config.getString("scriptDir"));
        if (!scriptFolder.isDirectory()) {
            scriptFolder.mkdirs();
        }
    }

    // *********************************
    //
    //        Getter and Setter
    //
    // *********************************

    public static TabooScript getInst() {
        return ins;
    }

    public static FileConfiguration getConf() {
        return config;
    }

    public File getScriptFolder() {
        return scriptFolder;
    }
}
