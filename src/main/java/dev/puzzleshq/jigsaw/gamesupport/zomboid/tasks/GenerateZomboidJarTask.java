package dev.puzzleshq.jigsaw.gamesupport.zomboid.tasks;

import dev.puzzleshq.jigsaw.gamesupport.zomboid.ZomboidPlugin;
import dev.puzzleshq.jigsaw.gamesupport.zomboid.ZomboidUtil;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;

public class GenerateZomboidJarTask extends DefaultTask {

    public GenerateZomboidJarTask() {
        setGroup("zomboid");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @TaskAction
    public void exec() {
        if (ZomboidPlugin.gameJar.exists()) return;
        try {
            ZomboidUtil.convertToJar(
                    getProject(),
                    ZomboidPlugin.gameJar,
                    new File(ZomboidPlugin.zomboidPath.toString())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
