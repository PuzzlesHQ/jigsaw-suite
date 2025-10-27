package dev.puzzleshq.jigsaw.publishing.config;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

public class MavenDeveloper {
    private final Property<String> id;
    private final Property<String> name;
    private final Property<String> email;

    @Inject
    public MavenDeveloper(ObjectFactory objects) {
        this.id = objects.property(String.class);
        this.name = objects.property(String.class);
        this.email = objects.property(String.class);
    }

    public Property<String> getId() { return id; }
    public Property<String> getName() { return name; }
    public Property<String> getEmail() { return email; }

    public void setId(String value) { id.set(value); }
    public void setName(String value) { name.set(value); }
    public void setEmail(String value) { email.set(value); }
}

