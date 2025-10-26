package dev.puzzleshq.jigsaw.publishing;

import dev.puzzleshq.jigsaw.inject.InjectionExtension;
import dev.puzzleshq.jigsaw.publishing.tasks.DependenciesJson;
import dev.puzzleshq.jigsaw.util.AbstractJigsawPlugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.tasks.TaskContainer;

public class Publishing extends AbstractJigsawPlugin {

    PublishingExtension publishingExtension;

    @Override
    public void apply(Project project) {
        super.apply(project);
        project.getPlugins().apply("maven-publish");
        project.getPlugins().apply("io.github.sgtsilvio.gradle.maven-central-publishing");

        TaskContainer container = project.getTasks();
        container.register("mkDeps", DependenciesJson.class);


        ExtensionContainer extensionContainer = project.getExtensions();
        publishingExtension = extensionContainer.create("jigsawPublishing", PublishingExtension.class, project, project.getObjects());



//        String ref = System.getenv("GITHUB_REF");
//        project.setVersion(((ref == null) ? "refs/tags/0.0.0-alpha" : ref).replaceAll("refs/tags/", ""));
//
//        String mavenUrl = System.getenv("MAVEN_URL");
//
//        String envRepo = System.getenv("MAVEN_REPO");
//        String mavenRepo = envRepo != null ? envRepo : "releases";
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
