package com.ilummc.tlib.scripting;

import com.ilummc.tlib.resources.TLocale;
import com.ilummc.tlib.scripting.bukkit.GroovyPlugin;
import com.ilummc.tlib.scripting.bukkit.GroovyPluginLoader;
import me.skymc.taboolib.commands.internal.BaseMainCommand;
import me.skymc.taboolib.commands.internal.BaseSubCommand;
import me.skymc.taboolib.commands.internal.type.CommandArgument;
import me.skymc.taboolib.commands.internal.type.CommandRegister;
import me.skymc.taboolib.fileutils.FileUtils;
import me.skymc.taboolib.plugin.PluginUnloadState;
import me.skymc.taboolib.plugin.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.Arrays;

/**
 * @Author sky
 * @Since 2018-07-07 15:52
 */
public class TabooScriptCommand extends BaseMainCommand {

    @Override
    public String getCommandTitle() {
        return TLocale.asString("PLUGIN_COMMAND_TITLE");
    }

    @CommandRegister(priority = 0)
    BaseSubCommand enable = new BaseSubCommand() {
        @Override
        public String getLabel() {
            return "load";
        }

        @Override
        public String getDescription() {
            return TLocale.asString("PLUGIN_COMMAND_ENABLE_DESCRIPTION");
        }

        @Override
        public CommandArgument[] getArguments() {
            return new CommandArgument[] {
                    new CommandArgument(TLocale.asString("PLUGIN_COMMAND_ENABLE_ARGUMENTS_1"))
            };
        }

        @Override
        public void onCommand(CommandSender sender, Command command, String s, String[] args) {
            String name = args[0].split("\\.")[0];
            String nameFully = name + ".groovy";
            if (GroovyPluginLoader.getPlugins().containsKey(name)) {
                TLocale.sendTo(sender, "PLUGIN_COMMAND_ENABLE_EXISTS", name);
                return;
            }
            File pluginFile = new File(TabooScript.getInst().getScriptFolder(), nameFully);
            if (!pluginFile.exists()) {
                pluginFile = new File(TabooScript.getInst().getScriptFolder(), "-" + nameFully);
                if (!pluginFile.exists()) {
                    TLocale.sendTo(sender, "PLUGIN_COMMAND_ENABLE_NOT_FOUND", name);
                    return;
                } else {
                    File newFile = FileUtils.file(TabooScript.getInst().getScriptFolder(), nameFully);
                    FileUtils.fileChannelCopy(pluginFile, newFile);
                    pluginFile.delete();
                    pluginFile = newFile;
                }
            }
            try {
                Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().loadPlugin(pluginFile));
                TLocale.sendTo(sender, "PLUGIN_COMMAND_ENABLE_SUCCESS", name);
            } catch (Exception e) {
                TLocale.sendTo(sender, "PLUGIN_COMMAND_ENABLE_FAILED", name, e.toString());
                e.printStackTrace();
            }
        }
    };

    @CommandRegister(priority = 1)
    BaseSubCommand disable = new BaseSubCommand() {
        @Override
        public String getLabel() {
            return "unload";
        }

        @Override
        public String getDescription() {
            return TLocale.asString("PLUGIN_COMMAND_DISABLE_DESCRIPTION");
        }

        @Override
        public CommandArgument[] getArguments() {
            return new CommandArgument[] {
                    new CommandArgument(TLocale.asString("PLUGIN_COMMAND_DISABLE_ARGUMENTS_1"))
            };
        }

        @Override
        public void onCommand(CommandSender sender, Command command, String s, String[] args) {
            String name = args[0].split("\\.")[0];
            GroovyPlugin plugin = GroovyPluginLoader.getPlugins().get(name);
            if (plugin == null) {
                TLocale.sendTo(sender, "PLUGIN_COMMAND_DISABLE_NOT_FOUND", name);
                return;
            }
            PluginUnloadState unload = PluginUtils.unload(plugin);
            if (unload.isFailed()) {
                TLocale.sendTo(sender, "PLUGIN_COMMAND_DISABLE_FAILED", name, unload.getMessage());
            } else {
                TLocale.sendTo(sender, "PLUGIN_COMMAND_DISABLE_SUCCESS", name);
                if (plugin.getFile().exists()) {
                    FileUtils.fileChannelCopy(plugin.getFile(), FileUtils.file(TabooScript.getInst().getScriptFolder(), "-" + name + ".groovy"));
                    plugin.getFile().delete();
                }
            }
        }
    };

    @CommandRegister(priority = 2)
    BaseSubCommand reload = new BaseSubCommand() {
        @Override
        public String getLabel() {
            return "reload";
        }

        @Override
        public String getDescription() {
            return TLocale.asString("PLUGIN_COMMAND_RELOAD_DESCRIPTION");
        }

        @Override
        public CommandArgument[] getArguments() {
            return new CommandArgument[] {
                    new CommandArgument(TLocale.asString("PLUGIN_COMMAND_RELOAD_ARGUMENTS_1"))
            };
        }

        @Override
        public void onCommand(CommandSender sender, Command command, String s, String[] args) {
            if (args[0].equalsIgnoreCase("config")) {
                TabooScript.getInst().reloadConfig();
                TLocale.sendTo(sender, "PLUGIN_COMMAND_RELOAD_CONFIG");
            } else if (args[0].equalsIgnoreCase("all")) {
                TabooScript.getInst().reloadConfig();
                GroovyPluginLoader.getPlugins().values().forEach(PluginUtils::unload);
                try {
                    Arrays.stream(Bukkit.getPluginManager().loadPlugins(TabooScript.getInst().getScriptFolder())).forEach(plugin -> Bukkit.getPluginManager().enablePlugin(plugin));
                } catch (Exception e) {
                    TLocale.sendTo(sender, "PLUGIN_COMMAND_RELOAD_FAILED", e.toString());
                }
                TLocale.sendTo(sender, "PLUGIN_COMMAND_RELOAD_ALL");
            } else {
                String name = args[0].split("\\.")[0];
                GroovyPlugin plugin = GroovyPluginLoader.getPlugins().get(name);
                if (plugin == null) {
                    TLocale.sendTo(sender, "PLUGIN_COMMAND_DISABLE_NOT_FOUND", name);
                    return;
                }
                PluginUnloadState unload = PluginUtils.unload(plugin);
                if (unload.isFailed()) {
                    TLocale.sendTo(sender, "PLUGIN_COMMAND_DISABLE_FAILED", name, unload.getMessage());
                } else if (!plugin.getFile().exists()) {
                    TLocale.sendTo(sender, "PLUGIN_COMMAND_ENABLE_NOT_FOUND", name, unload.getMessage());
                } else {
                    try {
                        Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().loadPlugin(plugin.getFile()));
                        TLocale.sendTo(sender, "PLUGIN_COMMAND_RELOAD_SUCCESS", name);
                    } catch (Exception e) {
                        TLocale.sendTo(sender, "PLUGIN_COMMAND_ENABLE_FAILED", name, e.toString());
                    }
                }
            }
        }
    };
}
