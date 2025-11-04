package dev.puzzleshq.jigsaw.gamesupport.zomboid.tasks;

import dev.puzzleshq.jigsaw.gamesupport.zomboid.ZomboidPlugin;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class DeleteZomboidJarTask extends DefaultTask {

    public DeleteZomboidJarTask() {
        setGroup("zomboid");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @TaskAction
    public void exec() {
        ZomboidPlugin.gameJar.delete();
    }

}
