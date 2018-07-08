package com.ilummc.tlib.scripting.groovy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.ilummc.tlib.resources.TLocale;
import com.ilummc.tlib.scripting.bukkit.EventRegistrar;
import com.ilummc.tlib.scripting.bukkit.GroovyDescription;
import com.ilummc.tlib.scripting.bukkit.GroovyPlugin;
import com.ilummc.tlib.scripting.script.InternalAPI;
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
import java.util.Optional;

public class GroovyProcessor extends GroovyProperty {

    private final GroovyPlugin plugin;

    private final InternalAPI api = InternalAPI.INSTANCE;

    private final Server bukkit = Bukkit.getServer();

    public GroovyProcessor(GroovyPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    private Closure onLoad = Closure.IDENTITY;
    private Closure onEnable = Closure.IDENTITY;
    private Closure onDisable = Closure.IDENTITY;

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
                if (map == null) {
                    map = new HashMap<>(0);
                }
                map.put(cmd, ImmutableMap.of());
                commandsF.set(plugin.getDescription(), map);
            } catch (Exception e) {
                TLocale.Logger.error("COMMAND_REGISTER_ERROR", e.toString());
            }
        }
    }

    public Closure getOnEnable() {
        return onEnable;
    }

    public void setOnEnable(@DelegatesTo(value = GroovyProcessor.class, strategy = Closure.DELEGATE_ONLY) Closure onEnable) {
        this.onEnable = onEnable.rehydrate(this, this, this);
    }

    public void onEnable(@DelegatesTo(value = GroovyProcessor.class, strategy = Closure.DELEGATE_ONLY) Closure onEnable) {
        setOnEnable(onEnable);
    }

    public Closure getOnLoad() {
        return onLoad;
    }

    public void setOnLoad(@DelegatesTo(value = GroovyProcessor.class, strategy = Closure.DELEGATE_ONLY) Closure onLoad) {
        this.onLoad = onLoad.rehydrate(this, this, this);
    }

    public void onLoad(@DelegatesTo(value = GroovyProcessor.class, strategy = Closure.DELEGATE_ONLY) Closure onLoad) {
        setOnLoad(onLoad);
    }

    public Closure getOnDisable() {
        return onDisable;
    }

    public void setOnDisable(@DelegatesTo(value = GroovyProcessor.class, strategy = Closure.DELEGATE_ONLY) Closure onDisable) {
        onDisable(onDisable);
    }

    public void onDisable(@DelegatesTo(value = GroovyProcessor.class, strategy = Closure.DELEGATE_ONLY) Closure onDisable) {
        this.onDisable = onDisable.rehydrate(this, this, this);
    }

    public void setDescription(@DelegatesTo(value = GroovyDescription.class, strategy = Closure.DELEGATE_ONLY) Closure description) {
        description(description);
    }

    private GroovyDescription spec = new GroovyDescription();

    public void description(@DelegatesTo(value = GroovyDescription.class, strategy = Closure.DELEGATE_ONLY) Closure description) {
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
        Optional<Class<?>> event = EventRegistrar.findEvent(name);
        if (event.isPresent()) {
            listen(event.get(), EventPriority.valueOf(priority), ignoreCancelled, closure);
        } else {
            TLocale.Logger.error("EVENT_INVALID", plugin.getName(), name);
        }
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
        return services.stream().filter(service -> service.getSimpleName().equals(name)).findFirst().map(service -> bukkit.getServicesManager().load(service)).orElse(null);
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
