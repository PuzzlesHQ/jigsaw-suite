package dev.puzzleshq.jigsaw.gamesupport.game;

import dev.puzzleshq.jigsaw.StringConstants;
import dev.puzzleshq.jigsaw.abstracts.AbstractJigsawExtension;
import dev.puzzleshq.jigsaw.gamesupport.game.tasks.building.BuildClientJarTask;
import dev.puzzleshq.jigsaw.gamesupport.game.tasks.building.BuildCommonJarTask;
import dev.puzzleshq.jigsaw.gamesupport.game.tasks.building.BuildServerJarTask;
import dev.puzzleshq.jigsaw.gamesupport.game.tasks.building.bundle.BuildMergedClientJarTask;
import dev.puzzleshq.jigsaw.gamesupport.game.tasks.building.bundle.BuildMergedJarTask;
import dev.puzzleshq.jigsaw.gamesupport.game.tasks.building.bundle.BuildMergedServerJarTask;
import dev.puzzleshq.jigsaw.gamesupport.game.tasks.building.bundle.BuildSingleJarTask;
import dev.puzzleshq.jigsaw.gamesupport.game.tasks.building.sources.BuildSimpleSourcesJarTask;
import dev.puzzleshq.jigsaw.gamesupport.game.tasks.building.sources.BuildSourcesJarTask;
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

        if (JigsawGame.COMMON_SOURCE_SET.getName().equals(StringConstants.COMMON_SIDE)) {
            container.register("buildCommonJar", BuildCommonJarTask.class);
        }

        if (JigsawGame.COMMON_SOURCE_SET.getName().equals(StringConstants.MAIN)) { // check for merged environment
            container.register("buildMergedJar", BuildSingleJarTask.class);
            container.register("buildSourcesJar", BuildSimpleSourcesJarTask.class);
        } else {
            container.register("buildMergedJar", BuildMergedJarTask.class);
            container.register("buildSourcesJar", BuildSourcesJarTask.class);
        }

        if (JigsawGame.CLIENT_SOURCE_SET.getName().equals(StringConstants.CLIENT_SIDE)) {
            container.register("buildClientJar", BuildClientJarTask.class);
            container.register("buildMergedClientJar", BuildMergedClientJarTask.class);
        }

        if (JigsawGame.SERVER_SOURCE_SET.getName().equals(StringConstants.SERVER_SIDE)) {
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
        JigsawGame.CLIENT_SOURCE_SET = sourceSetContainer.maybeCreate(StringConstants.CLIENT_SIDE);
        JigsawGame.COMMON_SOURCE_SET = sourceSetContainer.maybeCreate(StringConstants.COMMON_SIDE);
        JigsawGame.SERVER_SOURCE_SET = sourceSetContainer.maybeCreate(StringConstants.SERVER_SIDE);

        project.getDependencies().add(StringConstants.CLIENT_IMPLEMENTATION_CONFIGURATION, JigsawGame.COMMON_SOURCE_SET.getOutput());
        project.getDependencies().add(StringConstants.SERVER_IMPLEMENTATION_CONFIGURATION, JigsawGame.COMMON_SOURCE_SET.getOutput());

        updateTasks();

        JigsawGame.IS_SPLIT = true;
    }

    public void clientSourceSetOnly() {
        SourceSetContainer sourceSetContainer = project.getExtensions().getByType(SourceSetContainer.class);
        JigsawGame.CLIENT_SOURCE_SET = sourceSetContainer.maybeCreate(StringConstants.CLIENT_SIDE);
        JigsawGame.COMMON_SOURCE_SET = sourceSetContainer.maybeCreate(StringConstants.COMMON_SIDE);
        JigsawGame.SERVER_SOURCE_SET = JigsawGame.COMMON_SOURCE_SET;

        project.getDependencies().add(StringConstants.CLIENT_IMPLEMENTATION_CONFIGURATION, JigsawGame.COMMON_SOURCE_SET.getOutput());

        updateTasks();

        JigsawGame.IS_CLIENT_SPLIT = true;
    }

    public void serverSourceSetOnly() {
        SourceSetContainer sourceSetContainer = project.getExtensions().getByType(SourceSetContainer.class);
        JigsawGame.COMMON_SOURCE_SET = sourceSetContainer.maybeCreate(StringConstants.COMMON_SIDE);
        JigsawGame.SERVER_SOURCE_SET = sourceSetContainer.maybeCreate(StringConstants.SERVER_SIDE);
        JigsawGame.CLIENT_SOURCE_SET = JigsawGame.COMMON_SOURCE_SET;

        project.getDependencies().add(StringConstants.SERVER_IMPLEMENTATION_CONFIGURATION, JigsawGame.COMMON_SOURCE_SET.getOutput());

        updateTasks();

        JigsawGame.IS_SERVER_SPLIT = true;
    }

    public void mergedSourceSets() {
        SourceSetContainer sourceSetContainer = project.getExtensions().getByType(SourceSetContainer.class);
        JigsawGame.COMMON_SOURCE_SET = sourceSetContainer.maybeCreate(StringConstants.MAIN);
        JigsawGame.SERVER_SOURCE_SET = JigsawGame.COMMON_SOURCE_SET;
        JigsawGame.CLIENT_SOURCE_SET = JigsawGame.COMMON_SOURCE_SET;

        updateTasks();

        JigsawGame.IS_MERGED = true;
    }

}
