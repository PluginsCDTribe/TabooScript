package com.ilummc.tlib.scripting.groovy;

import com.google.common.collect.ImmutableList;
import com.ilummc.tlib.resources.TLocale;
import com.ilummc.tlib.scripting.bukkit.EventRegistrar;
import com.ilummc.tlib.scripting.bukkit.GroovyDescription;
import com.ilummc.tlib.scripting.bukkit.GroovyPlugin;
import com.ilummc.tlib.scripting.monitor.PluginMonitor;
import com.ilummc.tlib.scripting.script.InternalAPI;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import me.skymc.taboolib.commands.builder.SimpleCommandBuilder;
import me.skymc.taboolib.other.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class GroovyProcessor extends GroovyProperty {

    private final GroovyPlugin plugin;
    private final InternalAPI api = InternalAPI.INSTANCE;
    private final Server bukkit = Bukkit.getServer();

    private Closure onLoad = Closure.IDENTITY;
    private Closure onEnable = Closure.IDENTITY;
    private Closure onDisable = Closure.IDENTITY;

    private GroovyProcessor inst;
    private GroovyDescription description = new GroovyDescription();

    public GroovyProcessor(GroovyPlugin plugin) {
        super();
        this.inst = this;
        this.plugin = plugin;
    }

    public void onCommand(String cmd, Closure onCommand) {
        if (onCommand.getParameterTypes().length < 2) {
            TLocale.Logger.warn("COMMAND_ARGS_LENGTH");
        } else {
            SimpleCommandBuilder.create(cmd, plugin)
                    .silence()
                    .execute((sender, args) -> {
                        try {
                            Object call = onCommand.call(sender, args);
                            return call == null ? true : NumberUtils.getBoolean(String.valueOf(call));
                        } catch (Throwable e) {
                            PluginMonitor.printCommandError(plugin, e, cmd);
                            return true;
                        }
                    }).build();
        }
    }

    public void onTabCompleter(String cmd, Closure onTabCompleter) {
        PluginCommand command = Bukkit.getPluginCommand(cmd);
        if (command == null) {
            TLocale.Logger.error("COMMAND_NOT_REGISTER", cmd);
        } else if (onTabCompleter.getParameterTypes().length < 2) {
            TLocale.Logger.warn("COMMAND_ARGS_LENGTH");
        } else {
            command.setTabCompleter((sender, command1, label, args) -> {
                try {
                    return (List<String>) onTabCompleter.call(sender, args);
                } catch (Throwable e) {
                    PluginMonitor.printCommandError(plugin, e, command1.getName());
                    return ImmutableList.of();
                }
            });
        }
    }

    public Closure getOnEnable() {
        return onEnable;
    }

    public void setOnEnable(@DelegatesTo(value = GroovyProcessor.class, strategy = Closure.DELEGATE_FIRST) Closure onEnable) {
        this.onEnable = onEnable.rehydrate(this, this, this);
    }

    public void onEnable(@DelegatesTo(value = GroovyProcessor.class, strategy = Closure.DELEGATE_FIRST) Closure onEnable) {
        setOnEnable(onEnable);
    }

    public Closure getOnLoad() {
        return onLoad;
    }

    public void setOnLoad(@DelegatesTo(value = GroovyProcessor.class, strategy = Closure.DELEGATE_FIRST) Closure onLoad) {
        this.onLoad = onLoad.rehydrate(this, this, this);
    }

    public void onLoad(@DelegatesTo(value = GroovyProcessor.class, strategy = Closure.DELEGATE_FIRST) Closure onLoad) {
        setOnLoad(onLoad);
    }

    public Closure getOnDisable() {
        return onDisable;
    }

    public void setOnDisable(@DelegatesTo(value = GroovyProcessor.class, strategy = Closure.DELEGATE_FIRST) Closure onDisable) {
        onDisable(onDisable);
    }

    public void onDisable(@DelegatesTo(value = GroovyProcessor.class, strategy = Closure.DELEGATE_FIRST) Closure onDisable) {
        this.onDisable = onDisable.rehydrate(this, this, this);
    }

    public void setDescription(@DelegatesTo(value = GroovyDescription.class, strategy = Closure.DELEGATE_FIRST) Closure description) {
        description(description);
    }

    public void description(@DelegatesTo(value = GroovyDescription.class, strategy = Closure.DELEGATE_FIRST) Closure description) {
        Closure rehydrate = description.rehydrate(this.description, this, this);
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
        return description.toDescription(main);
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

    public BukkitTask asyncTask(int delay, int period, Closure closure) {
        return new GroovyTask(inst, closure).runTaskTimerAsynchronously(plugin, delay, period);
    }

    public BukkitTask asyncTask(int delay, Closure closure) {
        return new GroovyTask(inst, closure).runTaskLaterAsynchronously(plugin, delay);
    }

    public BukkitTask asyncTask(Closure closure) {
        return new GroovyTask(inst, closure).runTaskAsynchronously(plugin);
    }

    public BukkitTask task(int delay, int period, Closure closure) {
        return new GroovyTask(inst, closure).runTaskTimer(plugin, delay, period);
    }

    public BukkitTask task(int delay, Closure closure) {
        return new GroovyTask(inst, closure).runTaskLater(plugin, delay);
    }

    public BukkitTask task(Closure closure) {
        return new GroovyTask(inst, closure).runTask(plugin);
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

    private class GroovyTask extends BukkitRunnable {

        private final Closure run;

        private GroovyTask(GroovyProcessor owner, Closure closure) {
            this.run = closure.rehydrate(this, owner, owner);
        }

        @Override
        public void run() {
            try {
                run.call();
            } catch (Throwable e) {
                PluginMonitor.printTaskError(plugin, e);
            }
        }
    }
}
