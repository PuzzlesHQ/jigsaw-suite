package dev.puzzleshq.jigsaw.game;

import dev.puzzleshq.jigsaw.util.AbstractJigsawPlugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.tasks.SourceSet;

public class JigsawGame extends AbstractJigsawPlugin {

    private Configuration gameConfiguration;

    public static SourceSet CLIENT_SOURCE_SET;
    public static SourceSet COMMON_SOURCE_SET;
    public static SourceSet SERVER_SOURCE_SET;

    @Override
    public void apply(Project target) {
        super.apply(target);

        GameExtension extension = target.getExtensions().create("jigsawGame", GameExtension.class, target, target.getObjects());
        extension.splitSourceSets();
    }

    @Override
    public void afterEvaluate(Project project) {
        super.afterEvaluate(project);

        GameTasks.registerTasks(project);
    }

    @Override
    public String getName() {
        return "Jigsaw Game Plugin";
    }

    @Override
    public int getPriority() {
        return 4;
    }

}
