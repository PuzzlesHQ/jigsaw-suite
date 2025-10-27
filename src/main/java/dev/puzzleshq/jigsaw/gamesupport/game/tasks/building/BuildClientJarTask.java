package dev.puzzleshq.jigsaw.gamesupport.game.tasks.building;

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import dev.puzzleshq.jigsaw.StringConstants;
import dev.puzzleshq.jigsaw.gamesupport.game.JigsawGame;

import java.util.ArrayList;

public class BuildClientJarTask extends ShadowJar {

    public BuildClientJarTask() {
        setGroup("jigsaw/build");
        mergeServiceFiles();
        setConfigurations(new ArrayList(){{
            add(getProject().getConfigurations().getByName(StringConstants.CLIENT_BUNDLE_CONFIGURATION));
        }});

        getArchiveClassifier().set(StringConstants.CLIENT_SIDE);
        from(JigsawGame.CLIENT_SOURCE_SET.getOutput());
    }

}
