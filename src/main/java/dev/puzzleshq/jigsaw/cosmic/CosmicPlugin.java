package dev.puzzleshq.jigsaw.cosmic;

import dev.puzzleshq.jigsaw.util.AbstractJigsawPlugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.IvyArtifactRepository;

public class CosmicPlugin extends AbstractJigsawPlugin {

    @Override
    public void apply(Project target) {
        super.apply(target);

        RepositoryHandler handler = target.getRepositories();
        IvyArtifactRepository cr_archive = handler.ivy(repo -> { // The CR repo
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

    @Override
    public int getPriority() {
        return 5;
    }
}

