package dev.puzzleshq.jigsaw.gamesupport.game.tasks.building.bundle;

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import dev.puzzleshq.jigsaw.StringConstants;
import dev.puzzleshq.jigsaw.gamesupport.game.JigsawGame;
import org.gradle.api.file.FileCollection;

import java.util.ArrayList;
import java.util.List;

// build a jar with server and common merged
public class BuildMergedServerJarTask extends ShadowJar {

    public BuildMergedServerJarTask() {
        setGroup("jigsaw/build");
        mergeServiceFiles();
        List<FileCollection> configurations = new ArrayList<>();
        getProject().getConfigurations().all(c -> {
            if (c.getName().equals(StringConstants.SERVER_BUNDLE_CONFIGURATION)) {
                configurations.add(getProject().getConfigurations().getByName(StringConstants.SERVER_BUNDLE_CONFIGURATION));
            }
            if (c.getName().equals(StringConstants.COMMON_BUNDLE_CONFIGURATION)) {
                configurations.add(getProject().getConfigurations().getByName(StringConstants.COMMON_BUNDLE_CONFIGURATION));
            }
        });

        setConfigurations(configurations);

        getArchiveClassifier().set("server-merged");
        from(JigsawGame.SERVER_SOURCE_SET.getOutput());
        from(JigsawGame.COMMON_SOURCE_SET.getOutput());
    }

}
