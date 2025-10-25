package dev.puzzleshq.jigsaw.game;

import dev.puzzleshq.jigsaw.game.tasks.build.BuildClientJarTask;
import dev.puzzleshq.jigsaw.game.tasks.build.BuildCommonJarTask;
import dev.puzzleshq.jigsaw.game.tasks.build.BuildServerJarTask;
import dev.puzzleshq.jigsaw.game.tasks.build.bundle.BuildMergedClientJarTask;
import dev.puzzleshq.jigsaw.game.tasks.build.bundle.BuildMergedJarTask;
import dev.puzzleshq.jigsaw.game.tasks.build.bundle.BuildMergedServerJarTask;
import dev.puzzleshq.jigsaw.game.tasks.build.bundle.BuildSingleJarTask;
import dev.puzzleshq.jigsaw.game.tasks.build.sources.BuildSimpleSourcesJarTask;
import dev.puzzleshq.jigsaw.game.tasks.build.sources.BuildSourcesJarTask;
import dev.puzzleshq.jigsaw.util.AbstractJigsawExtension;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskContainer;

public class GameExtension extends AbstractJigsawExtension {

    public GameExtension(Project project, ObjectFactory objectFactory) {
        super(project, objectFactory);
    }

    public void updateTasks() {
        TaskContainer container = project.getTasks();

        if (JigsawGame.COMMON_SOURCE_SET.getName().equals("common")) {
            container.register("buildCommonJar", BuildCommonJarTask.class);
        }

        if (JigsawGame.COMMON_SOURCE_SET.getName().equals("main")) { // check for merged environment
            container.register("buildMergedJar", BuildSingleJarTask.class);
            container.register("buildSourcesJar", BuildSimpleSourcesJarTask.class);
        } else {
            container.register("buildMergedJar", BuildMergedJarTask.class);
            container.register("buildSourcesJar", BuildSourcesJarTask.class);
        }

        if (JigsawGame.CLIENT_SOURCE_SET.getName().equals("client")) {
            container.register("buildClientJar", BuildClientJarTask.class);
            container.register("buildMergedClientJar", BuildMergedClientJarTask.class);
        }

        if (JigsawGame.SERVER_SOURCE_SET.getName().equals("server")) {
            container.register("buildServerJar", BuildServerJarTask.class);
            container.register("buildMergedServerJar", BuildMergedServerJarTask.class);
        }

        JigsawGame.IS_SPLIT = false;
        JigsawGame.IS_MERGED = false;
        JigsawGame.IS_CLIENT_SPLIT = false;
        JigsawGame.IS_SERVER_SPLIT = false;
    }

    public void splitSourceSets() {
        SourceSetContainer sourceSetContainer = project.getExtensions().getByType(SourceSetContainer.class);
        JigsawGame.CLIENT_SOURCE_SET = sourceSetContainer.create("client");
        JigsawGame.COMMON_SOURCE_SET = sourceSetContainer.create("common");
        JigsawGame.SERVER_SOURCE_SET = sourceSetContainer.create("server");

        updateTasks();

        JigsawGame.IS_SPLIT = true;
    }

    public void clientSourceSetOnly() {
        SourceSetContainer sourceSetContainer = project.getExtensions().getByType(SourceSetContainer.class);
        JigsawGame.CLIENT_SOURCE_SET = sourceSetContainer.create("client");
        JigsawGame.COMMON_SOURCE_SET = sourceSetContainer.create("common");
        JigsawGame.SERVER_SOURCE_SET = JigsawGame.COMMON_SOURCE_SET;

        updateTasks();

        JigsawGame.IS_CLIENT_SPLIT = true;
    }

    public void serverSourceSetOnly() {
        SourceSetContainer sourceSetContainer = project.getExtensions().getByType(SourceSetContainer.class);
        JigsawGame.COMMON_SOURCE_SET = sourceSetContainer.create("common");
        JigsawGame.SERVER_SOURCE_SET = sourceSetContainer.create("server");
        JigsawGame.CLIENT_SOURCE_SET = JigsawGame.COMMON_SOURCE_SET;

        updateTasks();

        JigsawGame.IS_SERVER_SPLIT = true;
    }

    public void mergedSourceSets() {
        SourceSetContainer sourceSetContainer = project.getExtensions().getByType(SourceSetContainer.class);
        JigsawGame.COMMON_SOURCE_SET = sourceSetContainer.getByName("main");
        JigsawGame.SERVER_SOURCE_SET = JigsawGame.COMMON_SOURCE_SET;
        JigsawGame.CLIENT_SOURCE_SET = JigsawGame.COMMON_SOURCE_SET;

        updateTasks();

        JigsawGame.IS_MERGED = true;
    }

}
