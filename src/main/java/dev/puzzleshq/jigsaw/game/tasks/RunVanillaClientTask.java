package dev.puzzleshq.jigsaw.game.tasks;

import dev.puzzleshq.jigsaw.game.JigsawGame;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.tasks.JavaExec;

public class RunVanillaClientTask extends JavaExec {

    public RunVanillaClientTask() {
        setGroup("jigsaw");

        getMainClass().set("dev.puzzleshq.puzzleloader.loader.launch.pieces.ClientPiece");
        jvmArgs("-Dpuzzle.core.vanilla.execution=true");

        ConfigurableFileCollection collection = (ConfigurableFileCollection) getClasspath();

        collection.from(JigsawGame.CLIENT_SOURCE_SET.getRuntimeClasspath().getFiles());
        collection.from(JigsawGame.COMMON_SOURCE_SET.getRuntimeClasspath().getFiles());
    }

}
