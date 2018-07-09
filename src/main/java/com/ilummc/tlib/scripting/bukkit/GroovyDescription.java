package com.ilummc.tlib.scripting.bukkit;

import com.ilummc.tlib.scripting.TabooScript;
import org.bukkit.plugin.PluginDescriptionFile;

import java.lang.reflect.Field;
import java.util.Arrays;

public class GroovyDescription {

    private String name;
    private String version;
    private String website = "";
    private String description = "";
    private String[] depend = new String[0];
    private String[] softDepend = new String[0];
    private String[] authors = new String[0];

    public GroovyDescription name(String name) {
        this.name = TabooScript.getConf().getString("pluginPrefix") + name;
        return this;
    }

    public GroovyDescription version(String version) {
        this.version = version;
        return this;
    }

    public GroovyDescription depend(String... depend) {
        this.depend = depend;
        return this;
    }

    public GroovyDescription softdepend(String... softdepend) {
        this.softDepend = softdepend;
        return this;
    }

    public GroovyDescription website(String website) {
        this.website = website;
        return this;
    }

    public PluginDescriptionFile toDescription(Object main) {
        PluginDescriptionFile file = new PluginDescriptionFile(name == null ? TabooScript.getConf().getString("pluginPrefix") + main.getClass().getSimpleName() : name, version == null ? "1.0" : version, main.getClass().getName());
        setField(file, "rawName", file.getName());
        if (depend != null) {
            setField(file, "depend", Arrays.asList(depend));
        }
        if (softDepend != null) {
            setField(file, "softDepend", Arrays.asList(softDepend));
        }
        if (authors != null) {
            setField(file, "authors", Arrays.asList(authors));
        }
        if (website != null) {
            setField(file, "website", website);
        }
        if (description != null) {
            setField(file, "description", description);
        }
        return file;
    }

    private void setField(Object source, String field, Object target) {
        try {
            Field rawName = source.getClass().getDeclaredField(field);
            rawName.setAccessible(true);
            rawName.set(source, target);
        } catch (Exception ignored) {
        }
    }

    public GroovyDescription description(String description) {
        this.description = description;
        return this;
    }

    public GroovyDescription authors(String... authors) {
        this.authors = authors;
        return this;
    }

    public GroovyDescription author(String author) {
        this.authors = new String[] {author};
        return this;
    }
}
