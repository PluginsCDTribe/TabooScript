package com.ilummc.tlib.scripting.api;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Map;

public class ItemStackBuilder {

    private final ItemStack ins;

    public ItemStackBuilder() {
        ins = new ItemStack(Material.AIR);
    }

    public ItemStackBuilder potionEffect(@DelegatesTo(value = PotionEffectBuilder.class, strategy = Closure.DELEGATE_ONLY) Closure closure) {
        PotionMeta itemMeta = (PotionMeta) ins.getItemMeta();
        itemMeta.addCustomEffect(TabooScriptingApi.INSTANCE.effect(closure), true);
        ins.setItemMeta(itemMeta);
        return this;
    }

    public ItemStackBuilder potionEffect(Map<String, Object> potion) {
        PotionMeta itemMeta = (PotionMeta) ins.getItemMeta();
        itemMeta.addCustomEffect(new PotionEffect(potion), true);
        ins.setItemMeta(itemMeta);
        return this;
    }

    public ItemStackBuilder potionEffect(String effect, int level, int duration) {
        PotionMeta itemMeta = (PotionMeta) ins.getItemMeta();
        itemMeta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(effect), level, duration), true);
        ins.setItemMeta(itemMeta);
        return this;
    }

    public ItemStackBuilder potionEffect(String effect, int level, int duration, boolean ambient) {
        PotionMeta itemMeta = (PotionMeta) ins.getItemMeta();
        itemMeta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(effect), level, duration, ambient), true);
        ins.setItemMeta(itemMeta);
        return this;
    }

    public ItemStackBuilder potionEffect(String effect, int level, int duration, boolean ambient, boolean particle) {
        PotionMeta itemMeta = (PotionMeta) ins.getItemMeta();
        itemMeta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(effect), level, duration, ambient, particle), true);
        ins.setItemMeta(itemMeta);
        return this;
    }

    public ItemStackBuilder potionEffect(String effect, int level, int duration, boolean ambient, boolean particle, Color color) {
        PotionMeta itemMeta = (PotionMeta) ins.getItemMeta();
        itemMeta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(effect), level, duration, ambient, particle, color), true);
        ins.setItemMeta(itemMeta);
        return this;
    }

    public ItemStackBuilder unbreakable(boolean unbreakable) {
        ItemMeta itemMeta = ins.getItemMeta();
        try {
            itemMeta.setUnbreakable(unbreakable);
        } catch (Throwable t) {
            itemMeta.spigot().setUnbreakable(unbreakable);
        }
        ins.setItemMeta(itemMeta);
        return this;
    }

    public ItemStackBuilder removeFlag(String... flags) {
        ItemMeta itemMeta = ins.getItemMeta();
        itemMeta.removeItemFlags(Arrays.stream(flags).map(ItemFlag::valueOf).toArray(ItemFlag[]::new));
        ins.setItemMeta(itemMeta);
        return this;
    }

    public ItemStackBuilder flag(String... flags) {
        ItemMeta itemMeta = ins.getItemMeta();
        itemMeta.addItemFlags(Arrays.stream(flags).map(ItemFlag::valueOf).toArray(ItemFlag[]::new));
        ins.setItemMeta(itemMeta);
        return this;
    }

    public ItemStackBuilder lore(String... lore) {
        ItemMeta itemMeta = ins.getItemMeta();
        itemMeta.setLore(Arrays.asList(lore));
        ins.setItemMeta(itemMeta);
        return this;
    }

    public ItemStackBuilder name(String name) {
        ItemMeta itemMeta = ins.getItemMeta();
        itemMeta.setDisplayName(name);
        ins.setItemMeta(itemMeta);
        return this;
    }

    @Deprecated
    public ItemStackBuilder enchant(int enchant, int level) {
        ins.addUnsafeEnchantment(Enchantment.getById(enchant), level);
        return this;
    }

    public ItemStackBuilder enchant(Map<String, Integer> enchants) {
        enchants.forEach(this::enchant);
        return this;
    }

    public ItemStackBuilder removeEnchant(Enchantment enchantment) {
        ins.removeEnchantment(enchantment);
        return this;
    }

    public ItemStackBuilder removeEnchant(String enchantment) {
        ins.removeEnchantment(Enchantment.getByName(enchantment));
        return this;
    }

    public ItemStackBuilder enchant(Enchantment enchantment, int level) {
        ins.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemStackBuilder enchant(String enchantment, int level) {
        ins.addUnsafeEnchantment(Enchantment.getByName(enchantment), level);
        return this;
    }

    public ItemStackBuilder type(String type) {
        ins.setType(Material.matchMaterial(type));
        return this;
    }

    @Deprecated
    public ItemStackBuilder type(int id) {
        ins.setTypeId(id);
        return this;
    }

    public ItemStackBuilder amount(int amount) {
        ins.setAmount(amount);
        return this;
    }

    public ItemStackBuilder durability(int durability) {
        ins.setDurability((short) durability);
        return this;
    }

    @Deprecated
    public ItemStackBuilder data(int byteData) {
        if (ins.getType() != null)
            ins.setData(ins.getType().getNewData((byte) byteData));
        return this;
    }

    public ItemStack toItemStack() {
        return ins;
    }

}
