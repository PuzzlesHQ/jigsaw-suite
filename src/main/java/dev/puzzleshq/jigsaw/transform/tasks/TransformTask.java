package dev.puzzleshq.jigsaw.transform.tasks;

import dev.puzzleshq.jigsaw.transform.JigsawFileArtifact;
import dev.puzzleshq.jigsaw.transform.JigsawTransform;
import dev.puzzleshq.jigsaw.util.DepUtils;
import dev.puzzleshq.jigsaw.util.FileUtil;
import dev.puzzleshq.jigsaw.util.JarTransformer;
import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.artifacts.ExternalModuleDependency;
import org.gradle.api.artifacts.result.ResolvedArtifactResult;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.hash.Hashing;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

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

            for (ResolvedArtifactResult artifact : configuration.getIncoming().getArtifacts()) {
                String hash = Hashing.sha256().hashBytes(artifact.getFile().getName().getBytes()).toString();
                hash = hash.substring(0, 4) + hash.substring(hash.length() - 4);

                toBeTransformed.add(new JigsawFileArtifact(
                        artifact.getFile(),
                        stringConfigurationEntry.getKey(),
                        artifact.getId().getDisplayName(),
                        hash
                ));
            }

            while (!toBeTransformed.isEmpty()) {
                JigsawFileArtifact artifact = toBeTransformed.remove();
                File transformedFile = new File(JigsawTransform.transformCache, artifact.getLocalPath());
                transformedFile.getParentFile().mkdirs();
                if (transformedFile.exists())
                    transformedFile.delete();

                ((ExternalModuleDependency) Objects.requireNonNull(getProject().getDependencies()
                        .add(artifact.getConfiguration(), artifact.getNotation2()))).setChanging(true);
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
