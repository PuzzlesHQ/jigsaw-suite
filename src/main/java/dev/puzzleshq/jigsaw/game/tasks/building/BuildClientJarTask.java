package dev.puzzleshq.jigsaw.game.tasks.building;

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import dev.puzzleshq.jigsaw.game.JigsawGame;

import java.util.ArrayList;

public class BuildClientJarTask extends ShadowJar {

    public BuildClientJarTask() {
        setGroup("jigsaw/build");
        mergeServiceFiles();
        setConfigurations(new ArrayList(){{
            add(getProject().getConfigurations().getByName("clientBundle"));
        }});

        getArchiveClassifier().set("client");
        from(JigsawGame.CLIENT_SOURCE_SET.getOutput());
    }

}
