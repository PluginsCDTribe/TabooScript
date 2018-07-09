package com.ilummc.tlib.scripting.script;

import com.ilummc.tlib.scripting.TabooScript;
import me.skymc.taboolib.commands.TabooLibExecuteCommand;
import me.skymc.taboolib.display.ActionUtils;
import me.skymc.taboolib.display.TitleUtils;
import me.skymc.taboolib.economy.EcoUtils;
import me.skymc.taboolib.entity.EntityTag;
import me.skymc.taboolib.fileutils.FileUtils;
import me.skymc.taboolib.inventory.InventoryUtil;
import me.skymc.taboolib.itagapi.TagDataHandler;
import me.skymc.taboolib.json.tellraw.TellrawJson;
import me.skymc.taboolib.particle.EffLib;
import me.skymc.taboolib.playerdata.DataUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class TabooLibAPI {

    private final Plugin plugin;

    public TabooLibAPI(Plugin plugin) {
        this.plugin = plugin;
    }

    public void title(Player player, String main) {
        title(player, main, "");
    }

    public void title(Player player, String main, String sub) {
        title(player, main, sub, 10, 5, 10);
    }

    public void title(Player player, String main, String sub, int fadeIn, int stay, int fadeOut) {
        TitleUtils.sendTitle(player, main, sub, fadeIn, stay, fadeOut);
    }

    public void actionbar(Player player, String text) {
        ActionUtils.send(player, text);
    }

    public EffLib effectLib(String effect) {
        return EffLib.fromName(effect);
    }

    public TellrawJson createJson() {
        return TellrawJson.create();
    }

    public TagDataHandler playerTag() {
        return TagDataHandler.getHandler();
    }

    public EntityTag entityTag() {
        return EntityTag.getInst();
    }

    public double money(Player player) {
        return EcoUtils.get(player);
    }

    public void moneyGive(Player player, double money) {
        EcoUtils.add(player, money);
    }

    public void moneyTake(Player player, double money) {
        EcoUtils.remove(player, money);
    }

    public boolean itemHave(Player player, ItemStack item) {
        return InventoryUtil.hasItem(player, item, 1, false);
    }

    public boolean itemHave(Player player, ItemStack item, int amount) {
        return InventoryUtil.hasItem(player, item, amount, false);
    }

    public boolean itemHave(Player player, ItemStack item, int amount, boolean take) {
        return InventoryUtil.hasItem(player, item, amount, take);
    }

    public void executeCommand(CommandSender sender, String command) {
        TabooLibExecuteCommand.dispatchCommand(sender, command);
    }

    public FileConfiguration createData(String name, Plugin plugin) {
        return DataUtils.setPluginData(DataUtils.getFixedFileName(name), plugin, YamlConfiguration.loadConfiguration(FileUtils.file(plugin == null ? TabooScript.getInst().getScriptFolder() : plugin.getDataFolder(), DataUtils.getFixedFileName(name))));
    }

    public FileConfiguration createYaml(String name, Plugin plugin) {
        return YamlConfiguration.loadConfiguration(FileUtils.file(plugin == null ? TabooScript.getInst().getScriptFolder() : plugin.getDataFolder(), DataUtils.getFixedFileName(name)));
    }
}
