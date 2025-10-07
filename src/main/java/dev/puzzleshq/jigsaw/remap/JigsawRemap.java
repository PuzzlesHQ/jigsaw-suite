package dev.puzzleshq.jigsaw.remap;

import dev.puzzleshq.jigsaw.Plugins;
import dev.puzzleshq.jigsaw.util.AbstractJigsawPlugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.IvyArtifactRepository;
import org.gradle.api.artifacts.result.ResolvedArtifactResult;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class JigsawRemap extends AbstractJigsawPlugin {

    public static File mappingsDir;

    private Configuration configuration;

    @Override
    public void apply(@NotNull Project target) {
        super.apply(target);
//        if (mappingsDir == null) {
//            mappingsDir = new File(Plugins.jigsawDir, "mappings");
//        }
////                IvyArtifactRepository.MAVEN_ARTIFACT_PATTERN
//
//        RepositoryHandler repositories = target.getRepositories();
//        repositories.ivy(r -> {
//            r.setName("Forge SRG Mapping Repository");
//            r.setUrl("https://maven.minecraftforge.net/de/oceanlabs/mcp/mcp_config/");
//
//            r.metadataSources(s -> {
//                s.artifact();
//                s.ignoreGradleMetadataRedirection();
//            });
//
//            r.patternLayout(p -> {
//                p.setM2compatible(true);
//                p.artifact("[revision]/mcp_config-[revision].zip");
//            });
//
//            r.content(c -> {
//                c.includeModule("mappings.srg", "mcp_config");
//            });
//        });
//
//        repositories.ivy(r -> {
//            r.setName("Forge SRG Mapping Repository");
//            r.setUrl("https://repo.legacyfabric.net/legacyfabric/net/legacyfabric/yarn/");
//
//            r.metadataSources(s -> {
//                s.artifact();
//                s.ignoreGradleMetadataRedirection();
//            });
//
//            r.patternLayout(p -> {
//                p.setM2compatible(true);
//                p.artifact("[revision]/yarn-[revision].jar");
//            });
//
//            r.content(c -> {
//                c.includeModule("mappings.legacyfabric", "yarn");
//            });
//        });
//
//        target.getExtensions().create("jigsawRemap", RemappingExtension.class, target, target.getObjects());
//
//        ConfigurationContainer configurations = target.getConfigurations();
//        this.configuration = configurations.register("mappings").get();
        // https://maven.minecraftforge.net/de/oceanlabs/mcp/mcp_config/1.12.2-20201025.185735/mcp_config-1.12.2-20201025.185735.zip
        // https://maven.minecraftforge.net/de/oceanlabs/mcp/mcp_config/1.21.5-20250325.155543/mcp_config-1.21.5-20250325.155543.zip
        // https://maven.minecraftforge.net/de/oceanlabs/mcp/mcp_config/1.18.2-20220228.144236/mcp_config-1.18.2-20220228.144236.zip

    }

    @Override
    public void afterEvaluate(Project project) {
        super.afterEvaluate(project);

//        if (this.configuration.getDependencies().size() > 1) {
//            throw new RuntimeException("You can only declare 1 mapping dependency");
//        }
//        for (ResolvedArtifactResult artifact : this.configuration.getIncoming().getArtifacts()) {
//            System.out.println(artifact.getFile());
//        }
    }

    @Override
    public String getName() {
        return "Jigsaw Remapping Plugin";
    }

    @Override
    public int getPriority() {
        return 3;
    }
}
