package dev.puzzleshq.jigsaw.gamesupport.game.tasks.building;

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import dev.puzzleshq.jigsaw.StringConstants;
import dev.puzzleshq.jigsaw.gamesupport.game.JigsawGame;
import org.gradle.api.file.FileCollection;

import java.util.ArrayList;

public class BuildCommonJarTask extends ShadowJar {

    public BuildCommonJarTask() {
        setGroup("jigsaw/build");
        mergeServiceFiles();
        setConfigurations(new ArrayList<FileCollection>(){{
            add(getProject().getConfigurations().getByName(StringConstants.COMMON_BUNDLE_CONFIGURATION));
        }});

        getArchiveClassifier().set(StringConstants.COMMON_SIDE);
        from(JigsawGame.COMMON_SOURCE_SET.getOutput());
    }

}
