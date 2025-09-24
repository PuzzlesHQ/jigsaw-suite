package dev.puzzleshq.jigsaw.transform;

import dev.puzzleshq.jigsaw.util.AbstractJigsawExtension;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;

public class TransformExtension extends AbstractJigsawExtension {

    public TransformExtension(Project project, ObjectFactory objectFactory) {
        super(project, objectFactory);
    }

}
