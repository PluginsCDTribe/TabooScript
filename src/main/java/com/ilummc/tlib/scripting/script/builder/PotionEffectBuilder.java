package com.ilummc.tlib.scripting.script.builder;

import org.bukkit.Color;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.Locale;

public class PotionEffectBuilder {

    private String type;

    private int level = 0, duration = 0;

    private boolean ambient = false, particle = false;

    private Color color = Color.WHITE;

    public PotionEffectBuilder type(String type) {
        this.type = type;
        return this;
    }

    public PotionEffectBuilder level(int level) {
        this.level = level;
        return this;
    }

    public PotionEffectBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    public PotionEffectBuilder ambient(boolean ambient) {
        this.ambient = ambient;
        return this;
    }

    public PotionEffectBuilder particle(boolean particle) {
        this.particle = particle;
        return this;
    }

    public PotionEffectBuilder color(String color) {
        this.color = getColor(color);
        return this;
    }

    public PotionEffectBuilder color(Color color) {
        this.color = color;
        return this;
    }

    public PotionEffect toPotionEffect() {
        return new PotionEffect(PotionEffectType.getByName(type), level, duration, ambient, particle, color);
    }

    private static Color getColor(String color) {
        try {
            Field field = Color.class.getDeclaredField(color.toUpperCase(Locale.ENGLISH));
            return (Color) field.get(null);
        } catch (Exception e) {
            return Color.WHITE;
        }
    }

}
