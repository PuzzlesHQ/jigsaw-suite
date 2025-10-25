package dev.puzzleshq.jigsaw.game.tasks.building;

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import dev.puzzleshq.jigsaw.game.JigsawGame;

import java.util.ArrayList;

public class BuildCommonJarTask extends ShadowJar {

    public BuildCommonJarTask() {
        setGroup("jigsaw/build");
        mergeServiceFiles();
        setConfigurations(new ArrayList(){{
            add(getProject().getConfigurations().getByName("commonBundle"));
        }});

        getArchiveClassifier().set("common");
        from(JigsawGame.COMMON_SOURCE_SET.getOutput());
    }

}
