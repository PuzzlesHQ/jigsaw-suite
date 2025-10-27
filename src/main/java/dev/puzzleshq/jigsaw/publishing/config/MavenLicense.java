package dev.puzzleshq.jigsaw.publishing.config;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

public class MavenLicense {
    private final Property<String> name;
    private final Property<String> url;
    private final Property<String> distribution;
    private final Property<String> comments;

    @Inject
    public MavenLicense(ObjectFactory objects) {
        this.name = objects.property(String.class);
        this.url = objects.property(String.class);
        this.distribution = objects.property(String.class);
        this.comments = objects.property(String.class);
    }

    public Property<String> getName() { return name; }
    public Property<String> getUrl() { return url; }
    public Property<String> getDistribution() { return distribution; }
    public Property<String> getComments() { return comments; }

    public void setName(String value) { name.set(value); }
    public void setUrl(String value) { url.set(value); }
    public void setDistribution(String value) { distribution.set(value); }
    public void setComments(String value) { comments.set(value); }
}

