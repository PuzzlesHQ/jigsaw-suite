package dev.puzzleshq.jigsaw.publishing.config;

import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

public class MavenRepo {
    private final String name;
    private final Property<String> repo;
    private final Property<String> url;
    private final Property<String> username;
    private final Property<String> password;

    @Inject
    public MavenRepo(String name, Project project, ObjectFactory objects) {
        this.name = name;
        this.repo = objects.property(String.class);
        this.url = objects.property(String.class);
        this.username = objects.property(String.class);
        this.password = objects.property(String.class);
    }

    public String getName() { return name; }
    public Property<String> getRepo() { return repo; }
    public Property<String> getUrl() { return url; }
    public Property<String> getUsername() { return username; }
    public Property<String> getPassword() { return password; }

    public void setRepo(String value) { repo.set(value); }
    public void setUrl(String value) { url.set(value); }
    public void setUsername(String value) { username.set(value); }
    public void setPassword(String value) { password.set(value); }
}
