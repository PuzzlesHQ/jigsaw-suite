package dev.puzzleshq.jigsaw.abstracts;

import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;

public abstract class AbstractJigsawExtension {

    protected final Project project;
    protected final ObjectFactory objectFactory;

    public AbstractJigsawExtension(Project project, ObjectFactory objectFactory) {
        this.project = project;
        this.objectFactory = objectFactory;
    }

    public Project getProject() {
        return project;
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }
}
