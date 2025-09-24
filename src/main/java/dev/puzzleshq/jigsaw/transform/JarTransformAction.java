package dev.puzzleshq.jigsaw.transform;

import dev.puzzleshq.jigsaw.util.JavaUtils;
import org.gradle.api.artifacts.transform.InputArtifact;
import org.gradle.api.artifacts.transform.TransformAction;
import org.gradle.api.artifacts.transform.TransformOutputs;
import org.gradle.api.artifacts.transform.TransformParameters;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.Provider;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

import java.io.*;
import java.util.function.Function;

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

        try {
            FileInputStream stream = new FileInputStream(inp);
            byte[] bytes = JavaUtils.readAllBytes(stream);
            stream.close();

            ClassReader classReader = new ClassReader();

            for (Function<ClassVisitor, ClassVisitor> value : JigsawTransform.PLUGIN_TRANSFORMER_MAP.values()) {
                value.apply();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
