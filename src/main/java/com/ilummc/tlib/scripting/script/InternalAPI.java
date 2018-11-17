package com.ilummc.tlib.scripting.script;

import com.ilummc.tlib.scripting.bukkit.GroovyPluginLoader;
import com.ilummc.tlib.scripting.script.builder.ItemStackBuilder;
import com.ilummc.tlib.scripting.script.builder.PotionEffectBuilder;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.GroovyObject;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;

public class InternalAPI {

    public static final InternalAPI INSTANCE = new InternalAPI();
    public static final HashMap<String, Object> variable = new HashMap();

    public GroovyObject script(String name) {
        return GroovyPluginLoader.getPlugins().get(name).getGroovyObject();
    }

    public HashMap<String, Object> getAll() {
        return variable;
    }

    public HashMap<String, Object> getVariables() {
        return variable;
    }

    public Object set(String key, Object value) {
        return value == null ? variable.remove(key) : variable.put(key, value);
    }

    public Object get(String key) {
        return variable.get(key);
    }

    public Object get(String key, Object def) {
        return variable.getOrDefault(key, def);
    }

    public Object setVariable(String key, Object value) {
        return value == null ? variable.remove(key) : variable.put(key, value);
    }

    public Object getVariable(String key) {
        return variable.get(key);
    }

    public Object getVariable(String key, Object def) {
        return variable.getOrDefault(key, def);
    }

    public PotionEffect effect(@DelegatesTo(value = PotionEffectBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        PotionEffectBuilder builder = new PotionEffectBuilder();
        closure.rehydrate(builder, this, this).call();
        return builder.toPotionEffect();
    }

    public ItemStack item(@DelegatesTo(value = ItemStackBuilder.class, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        ItemStackBuilder builder = new ItemStackBuilder();
        closure.rehydrate(builder, this, this).call();
        return builder.toItemStack();
    }
}
