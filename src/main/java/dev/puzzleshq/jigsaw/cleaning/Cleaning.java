package dev.puzzleshq.jigsaw.cleaning;

import dev.puzzleshq.jigsaw.abstracts.AbstractJigsawPlugin;
import dev.puzzleshq.jigsaw.cleaning.tasks.CleanJigsawGlobalTask;
import dev.puzzleshq.jigsaw.cleaning.tasks.CleanJigsawLocalTask;
import dev.puzzleshq.jigsaw.cleaning.tasks.CleanOldJigsawGlobalTask;
import dev.puzzleshq.jigsaw.cleaning.tasks.CleanOldJigsawLocalTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;

import java.io.File;

public class Cleaning extends AbstractJigsawPlugin {

    public static String group = "Jigsaw/Cleaning";

    public static File oldLocalJigsawDir;
    public static File oldGlobalJigsawDir;

    public Cleaning() {
        super("Cleaning");
    }

    @Override
    public void apply(Project project) {
        super.apply(project);
        oldLocalJigsawDir = project.file(".gradle/jigsaw-cache");
        oldGlobalJigsawDir = new File(project.getGradle().getGradleUserHomeDir(), "caches/jigsaw-gradle");

        TaskContainer container  = project.getTasks();
        container.register("cleanJigsawLocal", CleanJigsawLocalTask.class);
        container.register("cleanJigsawGlobal", CleanJigsawGlobalTask.class);
        container.register("cleanOldJigsawLocal", CleanOldJigsawLocalTask.class);
        container.register("cleanOldJigsawGlobal", CleanOldJigsawGlobalTask.class);

    }

    @Override
    public String getName() {
        return "Jigsaw Cleaning Plugin";
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
