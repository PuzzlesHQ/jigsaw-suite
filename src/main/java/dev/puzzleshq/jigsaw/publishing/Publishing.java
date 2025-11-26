package dev.puzzleshq.jigsaw.publishing;

import dev.puzzleshq.jigsaw.abstracts.AbstractJigsawPlugin;
import dev.puzzleshq.jigsaw.publishing.tasks.DependenciesJson;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskContainer;

public class Publishing extends AbstractJigsawPlugin {

    public final static String GROUP = "jigsaw/publishing";

    @Override
    public void apply(Project project) {
        super.apply(project);

        SourceSetContainer sourceSetContainer = project.getExtensions().getByType(SourceSetContainer.class);

        ConfigurationContainer configurations = project.getConfigurations();
        sourceSetContainer.all(sourceSet -> {
            if (sourceSet.getName().equals("main")) {
                configurations.register("includedDependency").get();
                return;
            }
            configurations.register(sourceSet.getName() + "IncludedDependency").get();
        });

        project.getPlugins().apply("maven-publish");
        project.getPlugins().apply("io.github.sgtsilvio.gradle.maven-central-publishing");

        TaskContainer container = project.getTasks();
        container.register("mkDeps", DependenciesJson.class);

    }

    @Override
    public String getName() {
        return "Jigsaw Publishing";
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
