package dev.puzzleshq.jigsaw.util;

import groovy.lang.Closure;
import org.gradle.api.Project;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.tasks.SourceSetContainer;

import java.util.function.Consumer;

public class GradleUtil {

    public static SourceSetContainer getSourceSetContainer(Project project) {
        return project.getExtensions().getByType(SourceSetContainer.class);
    }

    public static Gradle getGradle(Project project) {
        return project.getGradle();
    }

    public static void onSettingsEvaluate(Project project, Consumer<Settings> settingsConsumer) {
        getGradle(project).beforeSettings(settingsConsumer::accept);
    }

}
