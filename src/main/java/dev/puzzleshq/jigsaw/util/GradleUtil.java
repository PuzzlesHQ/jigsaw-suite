package dev.puzzleshq.jigsaw.util;

import org.gradle.api.Project;
import org.gradle.api.initialization.Settings;
import org.gradle.api.tasks.SourceSetContainer;

public class GradleUtil {

    public static SourceSetContainer getSourceSetContainer(Project project) {
        return project.getExtensions().getByType(SourceSetContainer.class);
    }

    public static Settings getGradleSettings(Project project) {
        return project.getExtensions().getByType(Settings.class);
    }

}
