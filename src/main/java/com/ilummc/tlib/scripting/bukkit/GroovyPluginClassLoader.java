package com.ilummc.tlib.scripting.bukkit;

import com.ilummc.tlib.scripting.TabooScript;
import groovy.lang.GroovyClassLoader;
import org.bukkit.plugin.Plugin;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.*;
import org.codehaus.groovy.control.CompilationUnit.SourceUnitOperation;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unchecked")
public final class GroovyPluginClassLoader extends GroovyClassLoader implements Comparable {

    static final Set<GroovyPluginClassLoader> LOADERS = new ConcurrentSkipListSet<>();
    private static final AtomicInteger INTEGER = new AtomicInteger(1);
    private static Map<String, Class<?>> classes;
    private final int id = INTEGER.getAndIncrement();
    private final Map<String, Class<?>> classMap = new ConcurrentHashMap<>();
    private String name;
    private ClassCollector collector;

    Plugin plugin;

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

    GroovyPluginClassLoader(String name) {
        super(GroovyPluginClassLoader.class.getClassLoader());
        String scriptName = name.substring(0, name.lastIndexOf('.'));
        String type = name.substring(name.lastIndexOf('.') + 1);
        this.name = type + "_" + scriptName;
    }

    void setPlugin(Plugin plugin) {
        this.plugin = plugin;
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
    protected ClassCollector createCollector(CompilationUnit unit, SourceUnit su) {
        InnerLoader loader = AccessController.doPrivileged((PrivilegedAction<InnerLoader>) () -> new InnerLoader(this));
        return collector = new Collector(loader, unit, su);
    }

    @Override
    public Class parseClass(String text) throws CompilationFailedException {
        return super.parseClass(text, TabooScript.getConf().getString("scriptPrefix") + name);
    }

    @Override
    protected CompilationUnit createCompilationUnit(CompilerConfiguration config, CodeSource source) {
        CompilationUnit compilationUnit = super.createCompilationUnit(config, source);
        compilationUnit.addPhaseOperation(new SourceUnitOperation() {
            @Override
            public void call(SourceUnit source) throws CompilationFailedException {
                ModuleNode ast = source.getAST();
                for (String className : TabooScript.getConf().getStringList("defaultImport")) {
                    if (className.endsWith(".*")) {
                        if (!alreadyStarImported(className.substring(0, className.length() - 1), ast)) {
                            ast.addStarImport(className.substring(0, className.length() - 1));
                        }
                    } else {
                        String simpleName = getSimpleClassName(className);
                        if (isClassValid(className) && !alreadyImported(simpleName, ast)) {
                            ast.addImport(simpleName, ClassHelper.make(className));
                        }
                    }
                }
            }
        }, Phases.CONVERSION);
        return compilationUnit;
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

    @Override
    public int compareTo(@Nullable Object o) {
        return o instanceof GroovyPluginClassLoader ? this.id - ((GroovyPluginClassLoader) o).id : -1;
    }

    // *********************************
    //
    //             Private
    //
    // *********************************

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

    private boolean alreadyImported(String clazz, ModuleNode ast) {
        return ast.getImport(clazz) != null;
    }

    private boolean alreadyStarImported(String packageName, ModuleNode ast) {
        return ast.getStarImports().stream().anyMatch(i -> i.getPackageName().equalsIgnoreCase(packageName));
    }

    private String getSimpleClassName(String clazz) {
        return clazz.substring(clazz.lastIndexOf('.') + 1);
    }

    private boolean isClassValid(String clazz) {
        try {
            loadClass(clazz, false);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static List<String> getClassName(String packageName) {
        String filePath = ClassLoader.getSystemResource("").getPath() + packageName.replace(".", "\\");
        return getClassName(filePath, null);
    }

    private static List<String> getClassName(String filePath, List<String> className) {
        List<String> list = new ArrayList<>();
        File file = new File(filePath);
        File[] childFiles = file.listFiles();
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                list.addAll(getClassName(childFile.getPath(), list));
            } else {
                String childFilePath = childFile.getPath();
                childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9, childFilePath.lastIndexOf("."));
                childFilePath = childFilePath.replace("\\", ".");
                list.add(childFilePath);
            }
        }
        return list;
    }
}
