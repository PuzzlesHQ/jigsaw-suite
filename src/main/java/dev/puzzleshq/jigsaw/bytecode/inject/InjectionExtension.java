package dev.puzzleshq.jigsaw.bytecode.inject;

import dev.puzzleshq.jigsaw.abstracts.AbstractJigsawExtension;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;

import java.io.File;

public class InjectionExtension extends AbstractJigsawExtension {

    public File modJson;

    public InjectionExtension(Project project, ObjectFactory objectFactory) {
        super(project, objectFactory);
    }

    public void setModJson(File file) {
        modJson = file;
        JigsawInject.files.add(file);
    }

}
