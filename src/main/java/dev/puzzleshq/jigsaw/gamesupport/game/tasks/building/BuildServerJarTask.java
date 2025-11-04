package dev.puzzleshq.jigsaw.gamesupport.game.tasks.building;

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import dev.puzzleshq.jigsaw.StringConstants;
import dev.puzzleshq.jigsaw.gamesupport.game.JigsawGame;
import org.gradle.api.file.FileCollection;

import java.util.ArrayList;

public class BuildServerJarTask extends ShadowJar {

    public BuildServerJarTask() {
        setGroup("jigsaw/build");
        setConfigurations(new ArrayList<FileCollection>(){{
            add(getProject().getConfigurations().getByName(StringConstants.SERVER_BUNDLE_CONFIGURATION));
        }});

        getArchiveClassifier().set(StringConstants.SERVER_SIDE);
        from(JigsawGame.SERVER_SOURCE_SET.getOutput());
    }

}
