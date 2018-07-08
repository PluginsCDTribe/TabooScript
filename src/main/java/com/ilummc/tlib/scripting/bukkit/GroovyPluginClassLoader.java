package com.ilummc.tlib.scripting.bukkit;

import com.ilummc.tlib.scripting.TabooScript;
import groovy.lang.GroovyClassLoader;
import org.bukkit.plugin.Plugin;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unchecked")
public final class GroovyPluginClassLoader extends GroovyClassLoader implements Comparable {

    private static final AtomicInteger INTEGER = new AtomicInteger(1);

    static final Set<GroovyPluginClassLoader> LOADERS = new ConcurrentSkipListSet<>();

    private static Map<String, Class<?>> classes;

    static {
        try {
            ClassLoader classLoader = GroovyPluginClassLoader.class.getClassLoader();
            Field field = classLoader.getClass().getDeclaredField("classes");
            field.setAccessible(true);
            Object o = field.get(classLoader);
            classes = (Map<String, Class<?>>) o;
        } catch (Exception e) {
            classes = new ConcurrentHashMap<>();
        }
    }

    private final int id = INTEGER.getAndIncrement();

    private final Map<String, Class<?>> classMap = new ConcurrentHashMap<>();

    Plugin plugin;

    private String name;

    GroovyPluginClassLoader(String name) {
        super(GroovyPluginClassLoader.class.getClassLoader());
        String scriptName = name.substring(0, name.lastIndexOf('.'));
        String type = name.substring(name.lastIndexOf('.') + 1);
        this.name = type + "_" + scriptName;
    }

    private ClassCollector collector;

    @Override
    protected ClassCollector createCollector(CompilationUnit unit, SourceUnit su) {
        InnerLoader loader = AccessController.doPrivileged((PrivilegedAction<InnerLoader>) () -> new InnerLoader(this));
        return collector = new Collector(loader, unit, su);
    }

    private class Collector extends ClassCollector {

        Collector(InnerLoader cl, CompilationUnit unit, SourceUnit su) {
            super(cl, unit, su);
        }

        @Override
        protected Class createClass(byte[] code, ClassNode classNode) {
            Class aClass = super.createClass(code, classNode);
            classMap.put(classNode.getName(), aClass);
            classes.put(classNode.getName(), aClass);
            return aClass;
        }

    }

    @Override
    public Class parseClass(String text) throws CompilationFailedException {
        return super.parseClass(text, TabooScript.getConf().getString("scriptPrefix") + name);
    }

    public static void clearClass(String name) {
        LOADERS.forEach(loader -> loader.removeClass(name));
    }

    public static void clearClasses() {
        LOADERS.forEach(GroovyPluginClassLoader::removeClasses);
    }

    public void removeClass(String name) {
        classes.remove(name);
        collector.getLoadedClasses().remove(classMap.remove(name));
        removeClassCacheEntry(name);
    }

    public void removeClasses() {
        classMap.forEach(classes::remove);
        classMap.clear();
        clearCache();
        collector.getLoadedClasses().clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GroovyPluginClassLoader)) {
            return false;
        }
        GroovyPluginClassLoader that = (GroovyPluginClassLoader) o;
        return id == that.id &&
                Objects.equals(classMap, that.classMap) &&
                Objects.equals(plugin, that.plugin) &&
                Objects.equals(name, that.name) &&
                Objects.equals(collector, that.collector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, classMap, plugin, name, collector);
    }

    void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int compareTo(@Nullable Object o) {
        return o instanceof GroovyPluginClassLoader ? this.id - ((GroovyPluginClassLoader) o).id : -1;
    }
}
