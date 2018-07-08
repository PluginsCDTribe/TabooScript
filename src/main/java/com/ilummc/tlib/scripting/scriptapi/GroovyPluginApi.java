package com.ilummc.tlib.scripting.scriptapi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.ilummc.tlib.resources.TLocale;
import com.ilummc.tlib.scripting.PropertyGroovyObject;
import com.ilummc.tlib.scripting.bukkit.DescriptionSpec;
import com.ilummc.tlib.scripting.bukkit.EventRegistrar;
import com.ilummc.tlib.scripting.bukkit.GroovyPlugin;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GroovyPluginApi extends PropertyGroovyObject {

    private final GroovyPlugin plugin;

    private final InternalApi api = InternalApi.INSTANCE;

    private final Server bukkit = Bukkit.getServer();

    public GroovyPluginApi(GroovyPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    private Closure onEnable = Closure.IDENTITY,
            onLoad = Closure.IDENTITY,
            onDisable = Closure.IDENTITY;

    public GroovyPlugin getBukkit() {
        return plugin;
    }

    @SuppressWarnings("unchecked")
    public void onCommand(String cmd, Closure onCommand) {
        if (onCommand.getParameterTypes().length < 2) {
            TLocale.Logger.warn("COMMAND_ARGS_LENGTH");
        } else {
            try {
                Field commandMapF = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                commandMapF.setAccessible(true);
                SimpleCommandMap commandMap = (SimpleCommandMap) commandMapF.get(Bukkit.getServer());
                Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
                constructor.setAccessible(true);
                PluginCommand command = constructor.newInstance(cmd, plugin);
                command.setExecutor((sender, command1, label, args) -> {
                    onCommand.call(sender, args);
                    return true;
                });
                command.setTabCompleter((sender, command12, alias, args) -> ImmutableList.of());
                commandMap.register(cmd, command);
                Field commandsF = plugin.getDescription().getClass().getDeclaredField("commands");
                commandsF.setAccessible(true);
                Map<String, Object> map = (Map) commandsF.get(plugin.getDescription());
                if (map == null) map = new HashMap<>(0);
                map.put(cmd, ImmutableMap.of());
                commandsF.set(plugin.getDescription(), map);
                TLocale.Logger.fine("COMMAND_REGISTERED", plugin.toString(), cmd);
            } catch (Exception e) {
                TLocale.Logger.error("COMMAND_REGISTER_ERROR", e.toString());
            }
        }
    }

    public Closure getOnEnable() {
        return onEnable;
    }

    public void setOnEnable(@DelegatesTo(value = GroovyPluginApi.class, strategy = Closure.DELEGATE_FIRST) Closure onEnable) {
        this.onEnable = onEnable.rehydrate(this, this, this);
    }

    public void onEnable(@DelegatesTo(value = GroovyPluginApi.class, strategy = Closure.DELEGATE_FIRST) Closure onEnable) {
        setOnEnable(onEnable);
    }

    public Closure getOnLoad() {
        return onLoad;
    }

    public void setOnLoad(@DelegatesTo(value = GroovyPluginApi.class, strategy = Closure.DELEGATE_FIRST) Closure onLoad) {
        this.onLoad = onLoad.rehydrate(this, this, this);
    }

    public void onLoad(@DelegatesTo(value = GroovyPluginApi.class, strategy = Closure.DELEGATE_FIRST) Closure onLoad) {
        setOnLoad(onLoad);
    }

    public Closure getOnDisable() {
        return onDisable;
    }

    public void setOnDisable(@DelegatesTo(value = GroovyPluginApi.class, strategy = Closure.DELEGATE_FIRST) Closure onDisable) {
        onDisable(onDisable);
    }

    public void onDisable(@DelegatesTo(value = GroovyPluginApi.class, strategy = Closure.DELEGATE_FIRST) Closure onDisable) {
        this.onDisable = onDisable.rehydrate(this, this, this);
    }

    public void setDescription(@DelegatesTo(value = DescriptionSpec.class, strategy = Closure.DELEGATE_ONLY) Closure description) {
        description(description);
    }

    private DescriptionSpec spec = new DescriptionSpec();

    public void description(@DelegatesTo(value = DescriptionSpec.class, strategy = Closure.DELEGATE_ONLY) Closure description) {
        Closure rehydrate = description.rehydrate(spec, this, this);
        rehydrate.call();
    }

    public void info(String x) {
        Bukkit.getLogger().info("[" + plugin.getName() + "] " + x);
    }

    public void warn(String x) {
        Bukkit.getConsoleSender().sendMessage("§6[" + plugin.getName() + "] " + x);
    }

    public void error(String x) {
        Bukkit.getConsoleSender().sendMessage("§c[" + plugin.getName() + "] " + x);
    }

    public PluginDescriptionFile toDescription(Object main) {
        return spec.toDescription(main);
    }

    public void listen(Closure closure) {
        if (closure.getParameterTypes().length > 0 && Event.class.isAssignableFrom(closure.getParameterTypes()[0])) {
            listen("NORMAL", closure);
        }
    }

    public void listen(String nameOrPriority, Closure closure) {
        if (closure.getParameterTypes().length > 0 && Event.class.isAssignableFrom(closure.getParameterTypes()[0])) {
            listen(nameOrPriority, false, closure);
        } else {
            listen(nameOrPriority, "NORMAL", closure);
        }
    }

    public void listen(String priority, boolean ignoreCancelled, Closure closure) {
        if (closure.getParameterTypes().length > 0 && Event.class.isAssignableFrom(closure.getParameterTypes()[0])) {
            listen(closure.getParameterTypes()[0], EventPriority.valueOf(priority), ignoreCancelled, closure);
        }
    }

    public void listen(String name, String priority, Closure closure) {
        listen(name, priority, false, closure);
    }

    public void listen(String name, String priority, boolean ignoreCancelled, Closure closure) {
        EventRegistrar.findEvent(name).ifPresent(clazz ->
                listen(clazz, EventPriority.valueOf(priority), ignoreCancelled, closure));
    }

    private void listen(Class eventClazz, EventPriority priority, boolean ignoreCancelled, Closure closure) {
        EventRegistrar.register(eventClazz, priority, ignoreCancelled, closure, plugin);
    }

    public void asyncTask(int delay, int period, Closure closure) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, closure::call, delay, period);
    }

    public void asyncTask(int delay, Closure closure) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, closure::call, delay);
    }

    public void asyncTask(Closure closure) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, closure::call);
    }

    public void task(int delay, int period, Closure closure) {
        Bukkit.getScheduler().runTaskTimer(plugin, closure::call, delay, period);
    }

    public void task(int delay, Closure closure) {
        Bukkit.getScheduler().runTaskLater(plugin, closure::call, delay);
    }

    public void task(Closure closure) {
        Bukkit.getScheduler().runTask(plugin, closure::call);
    }

    public Object service(String name) {
        Collection<Class<?>> services = bukkit.getServicesManager().getKnownServices();
        for (Class<?> service : services) {
            if (service.getSimpleName().equals(name))
                return bukkit.getServicesManager().load(service);
        }
        return null;
    }

    public void command(String cmd) {
        command(cmd, bukkit.getConsoleSender());
    }

    public void command(String cmd, CommandSender sender) {
        Bukkit.dispatchCommand(sender, cmd);
    }

    public void broadcast(String text) {
        Bukkit.broadcastMessage(text);
    }

}
