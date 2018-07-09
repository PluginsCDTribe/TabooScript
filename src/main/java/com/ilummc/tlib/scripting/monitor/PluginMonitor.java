package com.ilummc.tlib.scripting.monitor;

import com.ilummc.tlib.resources.TLocale;
import com.ilummc.tlib.scripting.bukkit.GroovyPlugin;

/**
 * @Author sky
 * @Since 2018-07-09 18:00
 */
public class PluginMonitor {

    public static void printError(GroovyPlugin plugin, Throwable e) {
        TLocale.Logger.error("PLUGIN_ERROR_OTHER", plugin.getName(), e.toString(), getErrorLine(plugin, e));
    }

    public static void printTaskError(GroovyPlugin plugin, Throwable e) {
        TLocale.Logger.error("PLUGIN_ERROR_TASK", plugin.getName(), e.toString(), getErrorLine(plugin, e));
    }

    public static void printEventError(GroovyPlugin plugin, Throwable e, String event) {
        TLocale.Logger.error("PLUGIN_ERROR_EVENT", event, plugin.getName(), e.toString(), getErrorLine(plugin, e));
    }

    public static void printEventRegisterError(GroovyPlugin plugin, Throwable e, String event, String message) {
        TLocale.Logger.error("PLUGIN_ERROR_EVENT_REGISTER", event, plugin.getName(), message, getErrorLine(plugin, e));
    }

    private static String getErrorLine(GroovyPlugin plugin, Throwable e) {
        int line = 0;
        for (int i = 0; i < e.getStackTrace().length; i++) {
            StackTraceElement element = e.getStackTrace()[i];
            if (plugin.getNameOrigin().equals(element.getFileName())) {
                line = element.getLineNumber();
                break;
            }
        }
        return line == 0 ? "?" : String.valueOf(line);
    }
}
