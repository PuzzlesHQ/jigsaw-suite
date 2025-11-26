package dev.puzzleshq.jigsaw.cleaning.tasks;

import dev.puzzleshq.jigsaw.cleaning.Cleaning;
import dev.puzzleshq.jigsaw.util.FileUtil;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class CleanOldJigsawGlobalTask extends DefaultTask {

    public CleanOldJigsawGlobalTask() {
        setGroup(Cleaning.group);
    }

    @TaskAction
    public void execute() {
        FileUtil.delete(Cleaning.oldGlobalJigsawDir);
    }


}
