package com.ilummc.tlib.scripting;

import com.ilummc.tlib.annotations.TConfig;
import org.bukkit.plugin.Plugin;

import java.io.File;

@TConfig
public class TsConfig {

    private String scriptDir = "./scripts";

    public String getScriptDir() {
        return scriptDir;
    }

    public File getDataFolder(Plugin plugin) {
        return new File(scriptDir + "/" + plugin.getName());
    }
}
