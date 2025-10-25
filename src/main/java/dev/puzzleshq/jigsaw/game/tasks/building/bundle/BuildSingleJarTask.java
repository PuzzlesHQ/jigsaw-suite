package dev.puzzleshq.jigsaw.game.tasks.building.bundle;

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import dev.puzzleshq.jigsaw.game.JigsawGame;
import org.gradle.api.file.FileCollection;

import java.util.ArrayList;
import java.util.List;

public class BuildSingleJarTask extends ShadowJar {

    public BuildSingleJarTask() {
        setGroup("jigsaw/build");
        mergeServiceFiles();

        List<FileCollection> configurations = new ArrayList<>();
        configurations.add(getProject().getConfigurations().getByName("bundle"));
        setConfigurations(configurations);

        getArchiveClassifier().set("merged");
        from(JigsawGame.COMMON_SOURCE_SET.getOutput());
    }

}
