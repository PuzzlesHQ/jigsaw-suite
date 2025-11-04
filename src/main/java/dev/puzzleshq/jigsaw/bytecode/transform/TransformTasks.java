package dev.puzzleshq.jigsaw.bytecode.transform;

import dev.puzzleshq.jigsaw.bytecode.transform.tasks.DeleteTransformsTask;
import dev.puzzleshq.jigsaw.bytecode.transform.tasks.TransformTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;

public class TransformTasks {

    public static void registerTasks(Project project) {
        TaskContainer container = project.getTasks();
        container.register("transformJars", TransformTask.class);
        container.register("deleteTransforms", DeleteTransformsTask.class);
    }

}
