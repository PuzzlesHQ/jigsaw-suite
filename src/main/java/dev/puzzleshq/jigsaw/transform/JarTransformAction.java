package dev.puzzleshq.jigsaw.transform;

import dev.puzzleshq.jigsaw.util.JarTransformer;
import org.gradle.api.artifacts.transform.InputArtifact;
import org.gradle.api.artifacts.transform.TransformAction;
import org.gradle.api.artifacts.transform.TransformOutputs;
import org.gradle.api.artifacts.transform.TransformParameters;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.Provider;

import java.io.*;

public abstract class JarTransformAction implements TransformAction<TransformParameters.None> {

    @InputArtifact
    public abstract Provider<FileSystemLocation> getInputArtifact();

    private static final String[] KNOWN_ANNOYING_JAR_FILES = {
            "commons-compress-",
            "javacord-",
            "sqlite-jdbc-"
    };

    @Override
    public void transform(TransformOutputs outputs) {
        File inp = getInputArtifact().get().getAsFile();
        File out = outputs.file(inp.getName().replace(".jar", "-transformed.jar"));

        JarTransformer.transform(inp, out, JigsawTransform.PLUGIN_TRANSFORMER_MAP.values());
    }
}
