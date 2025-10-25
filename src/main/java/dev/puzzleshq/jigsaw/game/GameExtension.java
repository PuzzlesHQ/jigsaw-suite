package dev.puzzleshq.jigsaw.game;

import dev.puzzleshq.jigsaw.util.AbstractJigsawExtension;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.SourceSetContainer;

public class GameExtension extends AbstractJigsawExtension {

    public GameExtension(Project project, ObjectFactory objectFactory) {
        super(project, objectFactory);
    }

    public void splitSourceSets() {
        SourceSetContainer sourceSetContainer = project.getExtensions().getByType(SourceSetContainer.class);
        JigsawGame.CLIENT_SOURCE_SET = sourceSetContainer.create("client");
        JigsawGame.COMMON_SOURCE_SET = sourceSetContainer.create("common");
        JigsawGame.SERVER_SOURCE_SET = sourceSetContainer.create("server");
    }

    public void clientSourceSetOnly() {
        SourceSetContainer sourceSetContainer = project.getExtensions().getByType(SourceSetContainer.class);
        JigsawGame.COMMON_SOURCE_SET = sourceSetContainer.create("client");
        JigsawGame.SERVER_SOURCE_SET = JigsawGame.COMMON_SOURCE_SET;
        JigsawGame.CLIENT_SOURCE_SET = JigsawGame.COMMON_SOURCE_SET;
    }

    public void serverSourceSetOnly() {
        SourceSetContainer sourceSetContainer = project.getExtensions().getByType(SourceSetContainer.class);
        JigsawGame.COMMON_SOURCE_SET = sourceSetContainer.create("server");
        JigsawGame.SERVER_SOURCE_SET = JigsawGame.COMMON_SOURCE_SET;
        JigsawGame.CLIENT_SOURCE_SET = JigsawGame.COMMON_SOURCE_SET;
    }

    public void mergedSourceSets() {
        SourceSetContainer sourceSetContainer = project.getExtensions().getByType(SourceSetContainer.class);
        JigsawGame.COMMON_SOURCE_SET = sourceSetContainer.create("main");
        JigsawGame.SERVER_SOURCE_SET = JigsawGame.COMMON_SOURCE_SET;
        JigsawGame.CLIENT_SOURCE_SET = JigsawGame.COMMON_SOURCE_SET;
    }

}
