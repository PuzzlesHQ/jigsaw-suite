package dev.puzzleshq.jigsaw.access;

import dev.puzzleshq.jigsaw.util.AbstractJigsawExtension;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.model.ObjectFactory;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

public class AccessExtension extends AbstractJigsawExtension {

    final AtomicReference<FileCollection> manipulators;

    public AccessExtension(Project project, ObjectFactory objectFactory) {
        super(project, objectFactory);

        this.manipulators = new AtomicReference<>(objectFactory.fileCollection());
    }

    public void addManipulators(ConfigurableFileCollection collection) {
        if (collection == null) return;
        for (File file : collection)
            project.evaluationDependsOn(file.getAbsolutePath());
        this.manipulators.set(this.manipulators.get().plus(collection));
    }

    public void addManipulators(ConfigurableFileCollection... collections) {
        if (collections == null) return;
        for (ConfigurableFileCollection collection : collections) {
            if (collection == null) continue;
            for (File file : collection)
                project.evaluationDependsOn(file.getAbsolutePath());
            this.manipulators.set(this.manipulators.get().plus(collection));
        }
    }

    public void addManipulators(File file) {
        if (file == null) return;
        this.manipulators.set(this.manipulators.get().plus(project.files(file)));
    }

    public void addManipulators(File... files) {
        if (files == null) return;
        for (File file : files) {
            if (file == null) continue;
            this.manipulators.set(this.manipulators.get().plus(project.files(file)));
        }
    }

    public Project getProject() {
        return project;
    }

    public void resetManipulators() {
        this.manipulators.set(objectFactory.fileCollection());
    }
}
