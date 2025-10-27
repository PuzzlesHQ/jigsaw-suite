package dev.puzzleshq.jigsaw.game.tasks.run;

import dev.puzzleshq.jigsaw.game.JigsawGame;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.tasks.JavaExec;

import java.io.File;

public class RunClientTask extends JavaExec {

    public RunClientTask() {
        setGroup("jigsaw/runs");
        args(
                "--mod-folder", "\"" + new File(JigsawGame.runDir, "pmods").getAbsolutePath() + "\""
        );
        getMainClass().set("dev.puzzleshq.puzzleloader.loader.launch.pieces.ClientPiece");

        dependsOn("buildMergedJar");

        ConfigurableFileCollection collection = (ConfigurableFileCollection) getClasspath();
        collection.from(JigsawGame.CLIENT_SOURCE_SET.getRuntimeClasspath().getFiles());
        collection.from(JigsawGame.COMMON_SOURCE_SET.getRuntimeClasspath().getFiles());

        workingDir(JigsawGame.runDir);
    }

}
