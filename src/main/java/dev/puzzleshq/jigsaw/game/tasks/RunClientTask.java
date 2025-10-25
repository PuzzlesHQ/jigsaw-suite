package dev.puzzleshq.jigsaw.game.tasks;

import dev.puzzleshq.jigsaw.game.JigsawGame;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.tasks.JavaExec;

public class RunClientTask extends JavaExec {

    public RunClientTask() {
        setGroup("jigsaw");

        getMainClass().set("dev.puzzleshq.puzzleloader.loader.launch.pieces.ClientPiece");

        ConfigurableFileCollection collection = (ConfigurableFileCollection) getClasspath();

        collection.from(JigsawGame.CLIENT_SOURCE_SET.getRuntimeClasspath().getFiles());
        collection.from(JigsawGame.COMMON_SOURCE_SET.getRuntimeClasspath().getFiles());
    }

}
