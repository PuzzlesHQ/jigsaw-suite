package dev.puzzleshq.jigsaw.gamesupport.cosmic;

import dev.puzzleshq.jigsaw.abstracts.AbstractSplitGamePlugin;
import org.gradle.api.Project;

public class CosmicPlugin extends AbstractSplitGamePlugin {

    public CosmicPlugin() {
        super("cosmicReach");
    }

    @Override
    public void apply(Project target) {
        super.apply(target);

        target.getRepositories().ivy(repo -> { // The CR repo
            repo.setName("CRArchive");
            repo.setUrl("https://github.com/PuzzlesHQ/CRArchive/releases/download");

            repo.patternLayout(pattern -> {
                pattern.artifact("/[revision]/cosmic-reach-[classifier]-[revision].jar");
                pattern.artifact("/[revision]/cosmic-reach-[classifier]-[revision].jar");
            });

            repo.metadataSources(sources -> {
                sources.artifact();
                sources.ignoreGradleMetadataRedirection();
            });

            repo.content(content -> {
                content.includeModule("finalforeach", "cosmic-reach");
            });
        });
    }


    @Override
    public String getName() {
        return "Jigsaw Cosmic";
    }

}

