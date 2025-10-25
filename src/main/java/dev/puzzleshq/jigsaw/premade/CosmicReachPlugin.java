package dev.puzzleshq.jigsaw.premade;

import dev.puzzleshq.jigsaw.access.JigsawAccess;
import dev.puzzleshq.jigsaw.cosmic.CosmicPlugin;
import dev.puzzleshq.jigsaw.game.JigsawGame;
import dev.puzzleshq.jigsaw.inject.JigsawInject;
import dev.puzzleshq.jigsaw.modloader.LoaderPlugin;
import dev.puzzleshq.jigsaw.sync.JigsawSync;
import dev.puzzleshq.jigsaw.transform.JigsawTransform;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class CosmicReachPlugin implements Plugin<Project> {

    @Override
    public void apply(Project target) {
        target.getPlugins().apply(JigsawSync.class);
        target.getPlugins().apply(JigsawTransform.class);
        target.getPlugins().apply(JigsawInject.class);
        target.getPlugins().apply(JigsawAccess.class);
        target.getPlugins().apply(JigsawGame.class);
        target.getPlugins().apply(LoaderPlugin.class);

        target.getPlugins().apply(CosmicPlugin.class);
    }

}
