package dev.puzzleshq.jigsaw.premade;

import dev.puzzleshq.jigsaw.bytecode.access.JigsawAccess;
import dev.puzzleshq.jigsaw.bytecode.extension.JigsawExtension;
import dev.puzzleshq.jigsaw.bytecode.inject.JigsawInject;
import dev.puzzleshq.jigsaw.bytecode.transform.JigsawTransform;
import dev.puzzleshq.jigsaw.cleaning.Cleaning;
import dev.puzzleshq.jigsaw.gamesupport.cosmic.CosmicPlugin;
import dev.puzzleshq.jigsaw.gamesupport.game.JigsawGame;
import dev.puzzleshq.jigsaw.modloader.LoaderPlugin;
import dev.puzzleshq.jigsaw.sync.JigsawSync;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class CosmicReachPlugin implements Plugin<Project> {

    @Override
    public void apply(Project target) {
        JigsawGame.isCosmicReach = true;

        target.getPlugins().apply(JigsawSync.class);
        target.getPlugins().apply(JigsawTransform.class);
        target.getPlugins().apply(JigsawInject.class);
        target.getPlugins().apply(JigsawExtension.class);
        target.getPlugins().apply(JigsawAccess.class);
        target.getPlugins().apply(JigsawGame.class);
        target.getPlugins().apply(LoaderPlugin.class);
        target.getPlugins().apply(Cleaning.class);

        target.getPlugins().apply(CosmicPlugin.class);
    }

}
