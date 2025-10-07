package dev.puzzleshq.jigsaw.zomboid;

import dev.puzzleshq.jigsaw.game.JigsawGame;
import dev.puzzleshq.jigsaw.util.AbstractJigsawExtension;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.SourceSetContainer;

public class ZomboidExtension extends AbstractJigsawExtension {

    public ZomboidExtension(Project project, ObjectFactory objectFactory) {
        super(project, objectFactory);
    }

    public void splitSourceSets() {
        SourceSetContainer sourceSetContainer = project.getExtensions().getByType(SourceSetContainer.class);
        JigsawGame.CLIENT_SOURCE_SET = sourceSetContainer.create("client");
        JigsawGame.COMMON_SOURCE_SET = sourceSetContainer.create("common");
        JigsawGame.SERVER_SOURCE_SET = sourceSetContainer.create("server");
    }

    public void mergedSourceSets() {
        SourceSetContainer sourceSetContainer = project.getExtensions().getByType(SourceSetContainer.class);
        JigsawGame.COMMON_SOURCE_SET = sourceSetContainer.create("main");
        JigsawGame.SERVER_SOURCE_SET = JigsawGame.COMMON_SOURCE_SET;
        JigsawGame.CLIENT_SOURCE_SET = JigsawGame.COMMON_SOURCE_SET;
    }

}
