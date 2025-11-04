package dev.puzzleshq.jigsaw.gamesupport.game.tasks.building.bundle;

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import dev.puzzleshq.jigsaw.StringConstants;
import dev.puzzleshq.jigsaw.gamesupport.game.JigsawGame;
import org.gradle.api.file.FileCollection;

import java.util.ArrayList;
import java.util.List;

public class BuildSingleJarTask extends ShadowJar {

    public BuildSingleJarTask() {
        setGroup("jigsaw/build");
        mergeServiceFiles();

        List<FileCollection> configurations = new ArrayList<>();
        configurations.add(getProject().getConfigurations().getByName(StringConstants.BUNDLE_CONFIGURATION));
        setConfigurations(configurations);

        getArchiveClassifier().set("merged");
        from(JigsawGame.COMMON_SOURCE_SET.getOutput());
    }

}
