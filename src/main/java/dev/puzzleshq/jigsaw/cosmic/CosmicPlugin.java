package dev.puzzleshq.jigsaw.cosmic;

import dev.puzzleshq.jigsaw.util.AbstractJigsawPlugin;
import org.gradle.api.Project;

public class CosmicPlugin extends AbstractJigsawPlugin {

    @Override
    public void apply(Project target) {
        super.apply(target);
    }

    @Override
    public String getName() {
        return "Jigsaw Cosmic";
    }

    @Override
    public int getPriority() {
        return 5;
    }
}

