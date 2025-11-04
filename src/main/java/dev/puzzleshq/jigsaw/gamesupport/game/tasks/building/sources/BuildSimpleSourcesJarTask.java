package dev.puzzleshq.jigsaw.gamesupport.game.tasks.building.sources;

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import dev.puzzleshq.jigsaw.gamesupport.game.JigsawGame;

public class BuildSimpleSourcesJarTask extends ShadowJar {

    public BuildSimpleSourcesJarTask() {
        setGroup("jigsaw/build");

        getArchiveClassifier().set("sources");

        from(JigsawGame.COMMON_SOURCE_SET.getAllSource());
    }

}
