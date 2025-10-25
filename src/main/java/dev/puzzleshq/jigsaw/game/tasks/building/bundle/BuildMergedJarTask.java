package dev.puzzleshq.jigsaw.game.tasks.building.bundle;

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import dev.puzzleshq.jigsaw.game.JigsawGame;
import org.gradle.api.file.FileCollection;

import java.util.ArrayList;
import java.util.List;

public class BuildMergedJarTask extends ShadowJar {

    public BuildMergedJarTask() {
        setGroup("jigsaw/build");
        mergeServiceFiles();
        List<FileCollection> configurations = new ArrayList<>();
        getProject().getConfigurations().all(c -> {
            if (c.getName().equals("clientBundle")) {
                configurations.add(getProject().getConfigurations().getByName("clientBundle"));
            }
            if (c.getName().equals("serverBundle")) {
                configurations.add(getProject().getConfigurations().getByName("serverBundle"));
            }
            if (c.getName().equals("commonBundle")) {
                configurations.add(getProject().getConfigurations().getByName("commonBundle"));
            }
        });

        setConfigurations(configurations);

        getArchiveClassifier().set("merged");
        from(JigsawGame.CLIENT_SOURCE_SET.getOutput());
        from(JigsawGame.SERVER_SOURCE_SET.getOutput());
        from(JigsawGame.COMMON_SOURCE_SET.getOutput());
    }

}
