package dev.puzzleshq.jigsaw.gamesupport.zomboid;

import dev.puzzleshq.jigsaw.gamesupport.zomboid.tasks.DeleteZomboidJarTask;
import dev.puzzleshq.jigsaw.gamesupport.zomboid.tasks.GenerateZomboidJarTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;

public class ZomboidTasks {

    public static void registerTasks(Project project) {
        TaskContainer container = project.getTasks();
        container.register("generateZomboidJar", GenerateZomboidJarTask.class);
        container.register("deleteZomboidJar", DeleteZomboidJarTask.class);
    }

}
