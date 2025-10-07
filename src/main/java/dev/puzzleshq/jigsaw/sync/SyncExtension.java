package dev.puzzleshq.jigsaw.sync;

import dev.puzzleshq.jigsaw.util.AbstractJigsawExtension;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;

public class SyncExtension extends AbstractJigsawExtension {

    public SyncExtension(Project project, ObjectFactory objectFactory) {
        super(project, objectFactory);
    }

}
