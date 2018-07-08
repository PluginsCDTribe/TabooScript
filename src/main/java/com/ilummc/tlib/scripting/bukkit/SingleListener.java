package com.ilummc.tlib.scripting.bukkit;

import groovy.lang.Closure;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

public interface SingleListener extends Listener, EventExecutor {

    void execute(Object event) throws EventException;

    Class<? extends Event> getType();

    EventPriority getPriority();

    boolean ignoreCancelled();

    @Override
    default void execute(Listener listener, Event event) throws EventException {
        execute(event);
    }

    static SingleListener of(Class<? extends Event> clazz, EventPriority priority, boolean ignoreCancelled, Closure closure) {
        return new SingleListener() {
            @Override
            public void execute(Object event) throws EventException {
                try {
                    closure.call(event);
                } catch (Exception e) {
                    throw new EventException(e);
                }
            }

            @Override
            public Class<? extends Event> getType() {
                return clazz;
            }

            @Override
            public EventPriority getPriority() {
                return priority;
            }

            @Override
            public boolean ignoreCancelled() {
                return ignoreCancelled;
            }
        };
    }

}
