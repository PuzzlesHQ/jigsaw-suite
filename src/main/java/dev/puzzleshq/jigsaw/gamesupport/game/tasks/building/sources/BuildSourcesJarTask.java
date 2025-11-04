package dev.puzzleshq.jigsaw.gamesupport.game.tasks.building.sources;

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import dev.puzzleshq.jigsaw.StringConstants;
import dev.puzzleshq.jigsaw.gamesupport.game.JigsawGame;

public class BuildSourcesJarTask extends ShadowJar {

    public BuildSourcesJarTask() {
        setGroup("jigsaw/build");

        getArchiveClassifier().set("sources");

        from(JigsawGame.COMMON_SOURCE_SET.getAllSource());

        if (JigsawGame.CLIENT_SOURCE_SET.getName().equals(StringConstants.CLIENT_SIDE))
            from(JigsawGame.CLIENT_SOURCE_SET.getAllSource());
        if (JigsawGame.SERVER_SOURCE_SET.getName().equals(StringConstants.SERVER_SIDE))
            from(JigsawGame.SERVER_SOURCE_SET.getAllSource());

    }

}
