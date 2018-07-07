package com.ilummc.tlib.scripting;

import com.ilummc.tlib.annotations.Dependency;
import com.ilummc.tlib.annotations.Logger;
import com.ilummc.tlib.inject.TDependencyInjector;
import com.ilummc.tlib.logger.TLogger;
import com.ilummc.tlib.resources.TLocale;
import com.ilummc.tlib.scripting.api.TabooScriptApi;
import com.ilummc.tlib.scripting.bukkit.GroovyPluginLoader;
import com.ilummc.tlib.scripting.tlib.TabooLibApi;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Dependency(type = Dependency.Type.LIBRARY, maven = "org.codehaus.groovy:groovy:2.5.0")
@Dependency(type = Dependency.Type.LIBRARY, maven = "org.codehaus.groovy:groovy-bsf:2.5.0")
@Dependency(type = Dependency.Type.LIBRARY, maven = "org.ow2.asm:asm-commons:6.2")
public class TabooScriptMain extends JavaPlugin {

    private static TabooScriptMain ins;

    private TsConfig config;

    @Logger(level = 2)
    private TLogger logger;

    @Override
    public void onLoad() {
        ins = this;
        TDependencyInjector.inject(this, this);
        Bukkit.getPluginManager().registerInterface(GroovyPluginLoader.class);
        TLocale.Logger.info("LOADING_SCRIPTS");
        File file = new File(config.getScriptDir());
        if (!file.isDirectory()) file.mkdirs();
        Bukkit.getPluginManager().loadPlugins(file);
        TabooScriptApi.registerProperty("tlib", TabooLibApi.class);
    }

    public TsConfig getConf() {
        return config;
    }

    public static TabooScriptMain instance() {
        return ins;
    }

}
