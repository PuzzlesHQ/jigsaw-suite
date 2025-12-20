package dev.puzzleshq.jigsaw.gamesupport.game.tasks.run;

import dev.puzzleshq.jigsaw.gamesupport.game.JigsawGame;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.tasks.JavaExec;

import java.io.File;

public class RunServerTask extends JavaExec {

    public RunServerTask() {
        setGroup("jigsaw/runs");

        args(
                "--mod-folder", "\"" + new File(JigsawGame.runDir, "pmods").getAbsolutePath() + "\""
        );
        getMainClass().set("dev.puzzleshq.puzzleloader.loader.launch.pieces.ServerPiece");

        dependsOn("buildMergedJar");

        ConfigurableFileCollection collection = (ConfigurableFileCollection) getClasspath();
        collection.from(JigsawGame.SERVER_SOURCE_SET.getRuntimeClasspath().getFiles());
        collection.from(JigsawGame.COMMON_SOURCE_SET.getRuntimeClasspath().getFiles());

        setStandardInput(System.in);
        workingDir(JigsawGame.runDir);
    }

}
