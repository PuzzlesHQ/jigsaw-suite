package dev.puzzleshq.jigsaw.modloader.tasks;

import dev.puzzleshq.jigsaw.zomboid.ZomboidPlugin;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class RefreshPuzzleManifests extends DefaultTask {

    public RefreshPuzzleManifests() {
        setGroup("jigsaw");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @TaskAction
    public void exec() {
        ZomboidPlugin.gameJar.delete();
    }

}
