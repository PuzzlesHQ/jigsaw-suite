package dev.puzzleshq.jigsaw.game.tasks.building.sources;

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import dev.puzzleshq.jigsaw.game.JigsawGame;

public class BuildSourcesJarTask extends ShadowJar {

    public BuildSourcesJarTask() {
        setGroup("jigsaw/build");

        getArchiveClassifier().set("sources");

        from(JigsawGame.COMMON_SOURCE_SET.getAllSource());

        if (JigsawGame.CLIENT_SOURCE_SET.getName().equals("client"))
            from(JigsawGame.CLIENT_SOURCE_SET.getAllSource());
        if (JigsawGame.SERVER_SOURCE_SET.getName().equals("server"))
            from(JigsawGame.SERVER_SOURCE_SET.getAllSource());

    }

}
