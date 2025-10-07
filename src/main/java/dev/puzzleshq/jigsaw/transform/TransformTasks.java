package dev.puzzleshq.jigsaw.transform;

import dev.puzzleshq.jigsaw.transform.tasks.DeleteTransformsTask;
import dev.puzzleshq.jigsaw.transform.tasks.TransformTask;
import dev.puzzleshq.jigsaw.zomboid.tasks.DeleteZomboidJarTask;
import dev.puzzleshq.jigsaw.zomboid.tasks.GenerateZomboidJarTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;

public class TransformTasks {

    public static void registerTasks(Project project) {
        TaskContainer container = project.getTasks();
        container.register("transformJars", TransformTask.class);
        container.register("deleteTransforms", DeleteTransformsTask.class);
    }

}
