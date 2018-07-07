package com.ilummc.tlib.scripting.bukkit;

import org.bukkit.plugin.PluginDescriptionFile;

import java.lang.reflect.Field;
import java.util.Arrays;

public class DescriptionSpec {

    private String name, version, website, description;

    private String[] depend, softdepend, authors;

    public DescriptionSpec name(String name) {
        this.name = "#" + name;
        return this;
    }

    public DescriptionSpec version(String version) {
        this.version = version;
        return this;
    }

    public DescriptionSpec depend(String... depend) {
        this.depend = depend;
        return this;
    }

    public DescriptionSpec softdepend(String... softdepend) {
        this.softdepend = softdepend;
        return this;
    }

    public DescriptionSpec website(String website) {
        this.website = website;
        return this;
    }

    public PluginDescriptionFile toDescription(Object main) {
        PluginDescriptionFile file = new PluginDescriptionFile(name == null ? "#" + main.getClass().getSimpleName() : name,
                version == null ? "1.0" : version, main.getClass().getName());
        setField(file, "rawName", file.getName());
        if (depend != null) setField(file, "depend", Arrays.asList(depend));
        if (softdepend != null) setField(file, "softDepend", Arrays.asList(softdepend));
        if (authors != null) setField(file, "authors", Arrays.asList(authors));
        if (website != null) setField(file, "website", website);
        if (description != null) setField(file, "description", description);
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

    public DescriptionSpec description(String description) {
        this.description = description;
        return this;
    }

    public DescriptionSpec authors(String... authors) {
        this.authors = authors;
        return this;
    }

    public DescriptionSpec author(String author) {
        this.authors = new String[]{author};
        return this;
    }

}
