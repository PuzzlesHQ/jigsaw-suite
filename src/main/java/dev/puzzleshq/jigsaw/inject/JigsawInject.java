package dev.puzzleshq.jigsaw.inject;

import dev.puzzleshq.jigsaw.transform.JigsawTransform;
import dev.puzzleshq.jigsaw.util.AbstractJigsawPlugin;
import dev.puzzleshq.jigsaw.util.JavaUtils;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class JigsawInject extends AbstractJigsawPlugin {

    @Override
    public String getName() {
        return "Jigsaw Interface Inject Plugin";
    }

    @Override
    public int getPriority() {
        return 2;
    }

    InjectionExtension injectionExtension;

    @Override
    public void apply(Project target) {
        super.apply(target);

        ExtensionContainer extensionContainer = target.getExtensions();
        injectionExtension = extensionContainer.create("jigsawInject", InjectionExtension.class, target, target.getObjects());

        JigsawTransform.PLUGIN_RESOURCE_PREPROCESSOR_MAP.put(
                getName(),
                (resourceName, bytes) -> {
                    if (!Objects.equals(resourceName, "puzzle.mod.json")) return;

                    String str = new String(bytes);
                    JsonObject object = JsonValue.readHjson(str).asObject();
                    InterfaceInjector.search(object);
                }
        );

        JigsawTransform.PLUGIN_TRANSFORMER_MAP.put(
                getName(),
                (className, inputVisitor) -> new InterfaceInjector(inputVisitor)
        );
    }

    @Override
    public void afterEvaluate(Project project) {
        super.afterEvaluate(project);
        InterfaceInjector.interfaceMap.clear();

        if (injectionExtension.modJson != null) {
            try {
                FileInputStream stream = new FileInputStream(injectionExtension.modJson);
                String str = new String(JavaUtils.readAllBytes(stream));
                JsonObject object = JsonValue.readHjson(str).asObject();
                InterfaceInjector.search(object);
                stream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
