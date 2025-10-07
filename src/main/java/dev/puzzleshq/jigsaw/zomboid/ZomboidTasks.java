package dev.puzzleshq.jigsaw.zomboid;

import dev.puzzleshq.jigsaw.zomboid.tasks.DeleteZomboidJarTask;
import dev.puzzleshq.jigsaw.zomboid.tasks.GenerateZomboidJarTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;

public class ZomboidTasks {

    public static void registerTasks(Project project) {
        TaskContainer container = project.getTasks();
        container.register("generateZomboidJar", GenerateZomboidJarTask.class);
        container.register("deleteZomboidJar", DeleteZomboidJarTask.class);

    }

}
