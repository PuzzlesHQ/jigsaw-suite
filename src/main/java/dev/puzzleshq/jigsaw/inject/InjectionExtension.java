package dev.puzzleshq.jigsaw.inject;

import dev.puzzleshq.jigsaw.util.AbstractJigsawExtension;
import dev.puzzleshq.jigsaw.util.JavaUtils;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class InjectionExtension extends AbstractJigsawExtension {

    public File modJson;

    public InjectionExtension(Project project, ObjectFactory objectFactory) {
        super(project, objectFactory);
    }

    public void setModJson(File file) {
        modJson = file;
    }

}
