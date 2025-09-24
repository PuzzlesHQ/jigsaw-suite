package dev.puzzleshq.jigsaw.transform;

import dev.puzzleshq.jigsaw.Plugins;
import dev.puzzleshq.jigsaw.util.AbstractJigsawPlugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;
import org.objectweb.asm.ClassVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JigsawTransform extends AbstractJigsawPlugin {

    public static final Map<AbstractJigsawPlugin, Function<ClassVisitor, ClassVisitor>> PLUGIN_TRANSFORMER_MAP = new HashMap<>();

    public TransformExtension transformExtension;

    @Override
    public void apply(Project target) {
        Plugins.setup(target.getPlugins());

        ExtensionContainer extensionContainer = target.getExtensions();
        transformExtension = extensionContainer.create("jigsawTransform", TransformExtension.class, target, target.getObjects());
    }

    @Override
    public void afterEvaluate(Project project) {

    }

    @Override
    public String getName() {
        return "Jigsaw Transform Plugin";
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
