package dev.puzzleshq.jigsaw.remap;

import dev.puzzleshq.jigsaw.util.AbstractJigsawExtension;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.result.ResolvedArtifactResult;
import org.gradle.api.model.ObjectFactory;

public class RemappingExtension extends AbstractJigsawExtension {

    public RemappingExtension(Project project, ObjectFactory objectFactory) {
        super(project, objectFactory);
    }

    public void registerMappingConfiguration(
            String configuration,
            Object mappingDependency,
            Object baseJar
    ) {
        Configuration internal = project.getConfigurations().register(configuration + "_internal_mapping").get();
        project.getDependencies().add(configuration + "_internal_mapping", mappingDependency);
        for (ResolvedArtifactResult artifact : internal.getIncoming().getArtifacts()) {
            System.out.println(artifact.getFile());
        }
        Configuration refinement = project.getConfigurations().register(configuration).get();
    }

}
