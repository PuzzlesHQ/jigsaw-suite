package dev.puzzleshq.jigsaw.bytecode.extension;

import dev.puzzleshq.jigsaw.StringConstants;
import dev.puzzleshq.jigsaw.abstracts.AbstractJigsawPlugin;
import dev.puzzleshq.jigsaw.abstracts.IHashablePlugin;
import dev.puzzleshq.jigsaw.bytecode.transform.JigsawTransform;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JigsawExtension extends AbstractJigsawPlugin implements IHashablePlugin {

    public static List<File> files = new ArrayList<>();

    @Override
    public String getName() {
        return "Jigsaw Enum Extension Plugin";
    }

    @Override
    public int getPriority() {
        return 2;
    }

    ExtenderJigsawExtension extenderJigsawExtension;

    @Override
    public void apply(Project target) {
        super.apply(target);

        ExtensionContainer extensionContainer = target.getExtensions();
        extenderJigsawExtension = extensionContainer.create("jigsawExtension", ExtenderJigsawExtension.class, target, target.getObjects());
        this.extenderJigsawExtension.resetEnumExtenders();

        JigsawTransform.PLUGIN_RESOURCE_PREPROCESSOR_MAP.put(
                getName(),
                (resourceName, bytes, resourceMap) -> {
                    if (!Objects.equals(resourceName, StringConstants.PUZZLE_MOD_JSON)) return;

                    String str = new String(bytes);
                    JsonObject object = JsonValue.readHjson(str).asObject();
                    EnumExtender.search(object, resourceMap);
                }
        );

        JigsawTransform.PLUGIN_TRANSFORMER_MAP.put(
                getName(),
                (className, inputVisitor) -> new EnumClassVisitor(inputVisitor)
        );
    }

    @Override
    public void afterEvaluate(Project project) {
        super.afterEvaluate(project);
        EnumExtender.enumMap.clear();

        for (File file : this.extenderJigsawExtension.extensionFiles.get()) {
            if (file == null) continue;

            try {
                byte[] bytes = Files.readAllBytes(file.getAbsoluteFile().toPath());
                System.out.println("\u001B[1;94m\t ↳ \u001B[1;0mAdded file \u001B[0;36m\"" + file.getName() + "\"\u001B[0;0m");
                EnumExtender.addExtenderFile(new String(bytes));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public File[] getFilesToHash() {
        List<File> strings = new ArrayList<>(this.extenderJigsawExtension.extensionFiles.get().getFiles());
        strings.addAll(files);
        return strings.toArray(new File[0]);
    }

    @Override
    public void triggerChange(Project project) {
//        JigsawTransform.autoTransform(project);
    }

}
