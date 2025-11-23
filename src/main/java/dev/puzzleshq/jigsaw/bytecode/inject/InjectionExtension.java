package dev.puzzleshq.jigsaw.bytecode.inject;

import dev.puzzleshq.jigsaw.abstracts.AbstractJigsawExtension;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.model.ObjectFactory;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class InjectionExtension extends AbstractJigsawExtension {

    public File modJson;
    public final AtomicReference<FileCollection> injectionFiles;

    public InjectionExtension(Project project, ObjectFactory objectFactory) {
        super(project, objectFactory);
        injectionFiles = new AtomicReference<>(objectFactory.fileCollection());
    }

    public void setModJson(File file) {
        modJson = file;
        JigsawInject.files.add(file);
    }

    public void addInterfaceInjectors(ConfigurableFileCollection collection) {
        if (collection == null) return;
        this.injectionFiles.set(this.injectionFiles.get().plus(collection));
    }

    public void addInterfaceInjectors(ConfigurableFileCollection... collections) {
        if (collections == null) return;
        for (ConfigurableFileCollection collection : collections) {
            if (collection == null) continue;
            this.injectionFiles.set(this.injectionFiles.get().plus(collection));
        }
    }

    public void addInterfaceInjectors(File file) {
        if (file == null) return;
        this.injectionFiles.set(this.injectionFiles.get().plus(project.files(file)));
    }

    public void addInterfaceInjectors(File... files) {
        if (files == null) return;
        for (File file : files) {
            if (file == null) continue;
            this.injectionFiles.set(this.injectionFiles.get().plus(project.files(file)));
        }
    }

    public Project getProject() {
        return project;
    }

    public void resetInterfaceInjectors() {
        this.injectionFiles.set(objectFactory.fileCollection());
    }

}
