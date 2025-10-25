package dev.puzzleshq.jigsaw.game.tasks;

import dev.puzzleshq.jigsaw.game.JigsawGame;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.tasks.JavaExec;

public class RunServerTask extends JavaExec {

    public RunServerTask() {
        setGroup("jigsaw");

        getMainClass().set("dev.puzzleshq.puzzleloader.loader.launch.pieces.ServerPiece");

        ConfigurableFileCollection collection = (ConfigurableFileCollection) getClasspath();

        collection.from(JigsawGame.SERVER_SOURCE_SET.getRuntimeClasspath().getFiles());
        collection.from(JigsawGame.COMMON_SOURCE_SET.getRuntimeClasspath().getFiles());
    }

}
