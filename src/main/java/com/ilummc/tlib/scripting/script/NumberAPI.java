package com.ilummc.tlib.scripting.script;

import com.google.common.base.Preconditions;
import me.skymc.taboolib.other.NumberUtils;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * @Author sky
 * @Since 2018-07-08 21:04
 */
public class NumberAPI {

    private final Plugin plugin;

    public NumberAPI(Plugin plugin) {
        this.plugin = plugin;
    }

    public int[] range(int min, int max) {
        Preconditions.checkArgument(max > min, "The second number must longer than first one.");
        return IntStream.range(min, max + 1).toArray();
    }

    public int[] range(int max) {
        return IntStream.range(0, max + 1).toArray();
    }

    public Random random() {
        return new Random();
    }

    public Random random(long seed) {
        return new Random(seed);
    }

    public int randomInt(int num1, int num2) {
        return NumberUtils.getRandomInteger(num1, num2);
    }

    public double randomDouble(double num1, double num2) {
        return NumberUtils.getRandomDouble(num1, num2);
    }

    public double formatDouble(double origin) {
        return NumberUtils.format(origin);
    }

    public long hash(Object... object) {
        return Objects.hash(object);
    }
}
