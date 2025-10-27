package dev.puzzleshq.jigsaw.transform.tasks;

import dev.puzzleshq.jigsaw.transform.JigsawTransform;
import dev.puzzleshq.jigsaw.util.FileUtil;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class DeleteTransformsTask extends DefaultTask {

    public DeleteTransformsTask() {
        setGroup("jigsaw");
    }

    @TaskAction
    public void execute() {
        FileUtil.delete(JigsawTransform.transformCache);
    }

}
