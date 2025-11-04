package dev.puzzleshq.jigsaw.gamesupport.game.tasks.building.bundle;

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import dev.puzzleshq.jigsaw.StringConstants;
import dev.puzzleshq.jigsaw.gamesupport.game.JigsawGame;
import org.gradle.api.file.FileCollection;

import java.util.ArrayList;
import java.util.List;

// build a jar with client and common merged
public class BuildMergedClientJarTask extends ShadowJar {

    public BuildMergedClientJarTask() {
        setGroup("jigsaw/build");
        mergeServiceFiles();
        List<FileCollection> configurations = new ArrayList<>();
        getProject().getConfigurations().all(c -> {
            if (c.getName().equals(StringConstants.CLIENT_BUNDLE_CONFIGURATION)) {
                configurations.add(getProject().getConfigurations().getByName(StringConstants.CLIENT_BUNDLE_CONFIGURATION));
            }
            if (c.getName().equals(StringConstants.COMMON_BUNDLE_CONFIGURATION)) {
                configurations.add(getProject().getConfigurations().getByName(StringConstants.COMMON_BUNDLE_CONFIGURATION));
            }
        });

        setConfigurations(configurations);

        getArchiveClassifier().set("client-merged");
        from(JigsawGame.CLIENT_SOURCE_SET.getOutput());
        from(JigsawGame.COMMON_SOURCE_SET.getOutput());
    }

}
