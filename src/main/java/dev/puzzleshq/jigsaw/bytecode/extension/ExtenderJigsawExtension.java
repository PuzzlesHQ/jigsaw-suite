package dev.puzzleshq.jigsaw.bytecode.extension;

import dev.puzzleshq.jigsaw.abstracts.AbstractJigsawExtension;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.model.ObjectFactory;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

public class ExtenderJigsawExtension extends AbstractJigsawExtension {

    public final AtomicReference<FileCollection> extensionFiles;

    public ExtenderJigsawExtension(Project project, ObjectFactory objectFactory) {
        super(project, objectFactory);
        extensionFiles = new AtomicReference<>(objectFactory.fileCollection());
    }

    public void addEnumExtenders(ConfigurableFileCollection collection) {
        if (collection == null) return;
        this.extensionFiles.set(this.extensionFiles.get().plus(collection));
    }

    public void addEnumExtenders(ConfigurableFileCollection... collections) {
        if (collections == null) return;
        for (ConfigurableFileCollection collection : collections) {
            if (collection == null) continue;
            this.extensionFiles.set(this.extensionFiles.get().plus(collection));
        }
    }

    public void addEnumExtenders(File file) {
        if (file == null) return;
        this.extensionFiles.set(this.extensionFiles.get().plus(project.files(file)));
    }

    public void addEnumExtenders(File... files) {
        if (files == null) return;
        for (File file : files) {
            if (file == null) continue;
            this.extensionFiles.set(this.extensionFiles.get().plus(project.files(file)));
        }
    }

    public Project getProject() {
        return project;
    }

    public void resetEnumExtenders() {
        this.extensionFiles.set(objectFactory.fileCollection());
    }

}
