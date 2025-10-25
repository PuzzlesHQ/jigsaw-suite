package dev.puzzleshq.jigsaw.game.tasks.building;

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import dev.puzzleshq.jigsaw.game.JigsawGame;

import java.util.ArrayList;

public class BuildServerJarTask extends ShadowJar {

    public BuildServerJarTask() {
        setGroup("jigsaw/build");
        setConfigurations(new ArrayList(){{
            add(getProject().getConfigurations().getByName("serverBundle"));
        }});

        getArchiveClassifier().set("server");
        from(JigsawGame.SERVER_SOURCE_SET.getOutput());
    }

}
