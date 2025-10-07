package dev.puzzleshq.jigsaw.game;

import dev.puzzleshq.jigsaw.util.AbstractJigsawPlugin;
import dev.puzzleshq.jigsaw.zomboid.ZomboidExtension;
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

        ConfigurationContainer configurations = target.getConfigurations();
//        gameConfiguration = configurations.register("fatJarGame").get();

//        target.getExtensions().create("jigsawGame", ZomboidExtension.class, target, target.getObjects());
    }

    @Override
    public void afterEvaluate(Project project) {
        super.afterEvaluate(project);

//        gameConfiguration.getDependencies().all(d -> {
//            if (gameConfiguration.getDependencies().size() > 1)
//                throw new RuntimeException("Cannot have more than one 'fatJarGame' configuration per module.");
//
//            File file = gameConfiguration.getArtifacts().getFiles().getSingleFile();
//
//        });
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
