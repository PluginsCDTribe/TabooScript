package com.ilummc.tlib.scripting.scriptapi;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class InternalApi {

    public static final InternalApi INSTANCE = new InternalApi();

    public PotionEffect effect(@DelegatesTo(value = PotionEffectBuilder.class, strategy = Closure.DELEGATE_ONLY) Closure closure) {
        PotionEffectBuilder builder = new PotionEffectBuilder();
        closure.rehydrate(builder, this, this).call();
        return builder.toPotionEffect();
    }

    public ItemStack item(@DelegatesTo(value = ItemStackBuilder.class, strategy = Closure.DELEGATE_ONLY) Closure closure) {
        ItemStackBuilder builder = new ItemStackBuilder();
        closure.rehydrate(builder, this, this).call();
        return builder.toItemStack();
    }

}
