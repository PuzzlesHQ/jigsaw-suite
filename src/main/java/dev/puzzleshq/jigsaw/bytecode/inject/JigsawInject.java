package dev.puzzleshq.jigsaw.bytecode.inject;

import dev.puzzleshq.jigsaw.StringConstants;
import dev.puzzleshq.jigsaw.abstracts.AbstractJigsawPlugin;
import dev.puzzleshq.jigsaw.abstracts.IHashablePlugin;
import dev.puzzleshq.jigsaw.bytecode.transform.JigsawTransform;
import dev.puzzleshq.jigsaw.util.JavaUtils;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class JigsawInject extends AbstractJigsawPlugin implements IHashablePlugin {

    @Override
    public String getName() {
        return "Jigsaw Interface Inject Plugin";
    }

    @Override
    public int getPriority() {
        return 2;
    }

    InjectionExtension injectionExtension;
    static Set<File> files = new HashSet<>();

    @Override
    public void apply(Project target) {
        super.apply(target);

        ExtensionContainer extensionContainer = target.getExtensions();
        injectionExtension = extensionContainer.create("jigsawInject", InjectionExtension.class, target, target.getObjects());

        JigsawTransform.PLUGIN_RESOURCE_PREPROCESSOR_MAP.put(
                getName(),
                (resourceName, bytes) -> {
                    if (!Objects.equals(resourceName, StringConstants.PUZZLE_MOD_JSON)) return;

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

    @Override
    public File[] getFilesToHash() {
        return files.toArray(new File[0]);
    }

    @Override
    public void triggerChange(Project project) {
        JigsawTransform.autoTransform(project);
    }

}
