package dev.puzzleshq.jigsaw.bytecode.transform.tasks;

import dev.puzzleshq.jigsaw.bytecode.transform.JarTransformer;
import dev.puzzleshq.jigsaw.bytecode.transform.JigsawFileArtifact;
import dev.puzzleshq.jigsaw.bytecode.transform.JigsawTransform;
import dev.puzzleshq.jigsaw.util.FileUtil;
import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.*;
import org.gradle.api.artifacts.component.ModuleComponentIdentifier;
import org.gradle.api.artifacts.result.ResolvedArtifactResult;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.component.external.model.ModuleComponentArtifactIdentifier;

import java.io.File;
import java.util.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class TransformTask extends DefaultTask {

    public TransformTask() {
        setGroup("jigsaw");
    }

    @TaskAction
    public void execute() {
        FileUtil.delete(JigsawTransform.transformCache);

        for (Map.Entry<String, Configuration> stringConfigurationEntry : JigsawTransform.configurationMap.entrySet()) {
            Configuration configuration = stringConfigurationEntry.getValue();
            DependencySet dependencies = configuration.getAllDependencies();
            if (dependencies.isEmpty()) continue;

            Queue<JigsawFileArtifact> toBeTransformed = new ArrayDeque<>();

            ResolvedConfiguration resolvedConfiguration = configuration.getResolvedConfiguration();
            Set<ResolvedArtifact> resolvedArtifacts = resolvedConfiguration.getResolvedArtifacts();

            for (ResolvedArtifactResult artifact : configuration.getIncoming().getArtifacts()) {
                if (artifact.getId() instanceof ModuleComponentArtifactIdentifier) continue;

                toBeTransformed.add(new JigsawFileArtifact(
                        artifact.getFile(),
                        stringConfigurationEntry.getKey(),
                        artifact.getId().toString(),
                        true
                ));
            }
            for (ResolvedArtifact resolvedArtifact : resolvedArtifacts) {
                if (resolvedArtifact.getId().getComponentIdentifier() instanceof ModuleComponentIdentifier) {
                    ModuleComponentIdentifier identifier = (ModuleComponentIdentifier) resolvedArtifact.getId().getComponentIdentifier();
                    String group = identifier.getGroup();
                    String artifact = identifier.getModule();
                    String version = identifier.getVersion();
                    String classifier = resolvedArtifact.getClassifier();
                    String extension = resolvedArtifact.getExtension();

                    String notation = group + ":" + artifact + ":" + version + ":" + classifier + "@" + extension;

                    toBeTransformed.add(new JigsawFileArtifact(
                            resolvedArtifact.getFile(),
                            stringConfigurationEntry.getKey(),
                            notation,
                            false
                    ));
                }
            }

            while (!toBeTransformed.isEmpty()) {
                JigsawFileArtifact artifact = toBeTransformed.remove();
                File transformedFile = new File(JigsawTransform.transformCache, artifact.getLocalPath());
                transformedFile.getParentFile().mkdirs();
                if (transformedFile.exists())
                    transformedFile.delete();

                ((ExternalModuleDependency) Objects.requireNonNull(getProject().getDependencies()
                        .add(artifact.getConfiguration(), artifact.getTransformedNotation()))).setChanging(true);
                JarTransformer.process(
                        artifact.getRegularFile(),
                        JigsawTransform.PLUGIN_CLASS_PREPROCESSOR_MAP.values(),
                        JigsawTransform.PLUGIN_RESOURCE_PREPROCESSOR_MAP.values()
                );
                JarTransformer.transform(artifact.getRegularFile(), transformedFile, JigsawTransform.PLUGIN_TRANSFORMER_MAP.values());
            }
        }
    }

}
