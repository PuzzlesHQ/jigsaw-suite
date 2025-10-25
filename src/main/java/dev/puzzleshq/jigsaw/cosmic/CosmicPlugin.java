package dev.puzzleshq.jigsaw.cosmic;

import dev.puzzleshq.jigsaw.game.JigsawGame;
import dev.puzzleshq.jigsaw.util.AbstractJigsawPlugin;
import dev.puzzleshq.jigsaw.util.ConfigurationUtil;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
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

        target.getConfigurations().register("cosmicReach").get();
    }

    @Override
    public void afterEvaluate(Project project) {
        super.afterEvaluate(project);

        Configuration clientImpl = ConfigurationUtil.getClientTransformConfiguration(project);
        Configuration commonImpl = ConfigurationUtil.getCommonTransformConfiguration(project);
        Configuration serverImpl = ConfigurationUtil.getServerTransformConfiguration(project);

        DependencyHandler dependencyHandler = project.getDependencies();
        project.getConfigurations().all(a -> { // I have to do this so it doesn't crash when it can't find it
            if (a.getName().equals("cosmicReach")) {
                for (Dependency puzzleLoader : project.getConfigurations().getByName("cosmicReach").getDependencies()) {
                    if (JigsawGame.IS_MERGED) {
                        assert commonImpl != null;
                        dependencyHandler.add(commonImpl.getName(), puzzleLoader.getGroup() + ":" + puzzleLoader.getName() + ":" + puzzleLoader.getVersion() + ":client");
                        continue;
                    }

                    if (JigsawGame.IS_SPLIT) {
                        assert clientImpl != null;
                        dependencyHandler.add(clientImpl.getName(), puzzleLoader.getGroup() + ":" + puzzleLoader.getName() + ":" + puzzleLoader.getVersion() + ":client");
                        assert commonImpl != null;
                        dependencyHandler.add(commonImpl.getName(), puzzleLoader.getGroup() + ":" + puzzleLoader.getName() + ":" + puzzleLoader.getVersion() + ":server");
                        assert serverImpl != null;
                        dependencyHandler.add(serverImpl.getName(), puzzleLoader.getGroup() + ":" + puzzleLoader.getName() + ":" + puzzleLoader.getVersion() + ":server");
                        continue;
                    }

                    if (JigsawGame.IS_CLIENT_SPLIT) {
                        assert clientImpl != null;
                        dependencyHandler.add(clientImpl.getName(), puzzleLoader.getGroup() + ":" + puzzleLoader.getName() + ":" + puzzleLoader.getVersion() + ":client");
                        assert commonImpl != null;
                        dependencyHandler.add(commonImpl.getName(), puzzleLoader.getGroup() + ":" + puzzleLoader.getName() + ":" + puzzleLoader.getVersion() + ":client");
                        continue;
                    }

                    if (JigsawGame.IS_SERVER_SPLIT) {
                        assert serverImpl != null;
                        dependencyHandler.add(serverImpl.getName(), puzzleLoader.getGroup() + ":" + puzzleLoader.getName() + ":" + puzzleLoader.getVersion() + ":server");
                        assert commonImpl != null;
                        dependencyHandler.add(commonImpl.getName(), puzzleLoader.getGroup() + ":" + puzzleLoader.getName() + ":" + puzzleLoader.getVersion() + ":server");
                    }
                }
            }
        });
    }

    @Override
    public String getName() {
        return "Jigsaw Cosmic";
    }

    @Override
    public int getPriority() {
        return 6;
    }
}

