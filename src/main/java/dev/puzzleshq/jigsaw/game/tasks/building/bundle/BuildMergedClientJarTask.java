package dev.puzzleshq.jigsaw.game.tasks.building.bundle;

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import dev.puzzleshq.jigsaw.game.JigsawGame;
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
            if (c.getName().equals("clientBundle")) {
                configurations.add(getProject().getConfigurations().getByName("clientBundle"));
            }
            if (c.getName().equals("commonBundle")) {
                configurations.add(getProject().getConfigurations().getByName("commonBundle"));
            }
        });

        setConfigurations(configurations);

        getArchiveClassifier().set("client-merged");
        from(JigsawGame.CLIENT_SOURCE_SET.getOutput());
        from(JigsawGame.COMMON_SOURCE_SET.getOutput());
    }

}
