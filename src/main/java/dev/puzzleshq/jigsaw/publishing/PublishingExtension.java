package dev.puzzleshq.jigsaw.publishing;

import dev.puzzleshq.jigsaw.util.AbstractJigsawExtension;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;

public class PublishingExtension extends AbstractJigsawExtension {

    public PublishingExtension(Project project, ObjectFactory objectFactory) {
        super(project, objectFactory);
    }
}
