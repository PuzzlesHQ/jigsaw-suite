package dev.puzzleshq.jigsaw.game.tasks.run;

import dev.puzzleshq.jigsaw.game.JigsawGame;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.tasks.JavaExec;

import java.io.File;

public class RunVanillaClientTask extends JavaExec {

    public RunVanillaClientTask() {
        setGroup("jigsaw/runs");

        getMainClass().set("dev.puzzleshq.puzzleloader.loader.launch.pieces.ClientPiece");

        jvmArgs(
                "-Dpuzzle.core.disable-patching=true",
                "-Dpuzzle.core.disable-mod-search=true",
                "-Dpuzzle.core.disable-all-transform=true"
        );

        args(
                "--mod-folder", "\"" + new File(JigsawGame.runDir, "pmods").getAbsolutePath() + "\""
        );

        dependsOn("buildMergedJar");


        ConfigurableFileCollection collection = (ConfigurableFileCollection) getClasspath();
        collection.from(JigsawGame.CLIENT_SOURCE_SET.getRuntimeClasspath().getFiles());
        collection.from(JigsawGame.COMMON_SOURCE_SET.getRuntimeClasspath().getFiles());

        workingDir(JigsawGame.runDir);
    }

}
