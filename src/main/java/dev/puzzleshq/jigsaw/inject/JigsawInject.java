package dev.puzzleshq.jigsaw.inject;

import dev.puzzleshq.jigsaw.util.AbstractJigsawPlugin;
import org.gradle.api.Project;

public class JigsawInject extends AbstractJigsawPlugin {

    @Override
    public String getName() {
        return "Jigsaw Interface Inject Plugin";
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public void apply(Project target) {

    }

}
