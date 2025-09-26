package dev.puzzleshq.jigsaw.access;

import dev.puzzleshq.accesswriter.AccessWriters;
import dev.puzzleshq.accesswriter.api.IWriterFormat;
import dev.puzzleshq.accesswriter.file.ManipulationFile;
import dev.puzzleshq.accesswriter.transformers.AccessTransformerASM;
import dev.puzzleshq.jigsaw.Plugins;
import dev.puzzleshq.jigsaw.transform.JigsawTransform;
import dev.puzzleshq.jigsaw.util.AbstractJigsawPlugin;
import dev.puzzleshq.jigsaw.util.JavaUtils;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.ExtensionContainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class JigsawAccess extends AbstractJigsawPlugin {

    public AccessExtension accessExtension;

    @Override
    public void apply(Project target) {
        super.apply(target);

        ExtensionContainer extensionContainer = target.getExtensions();
        this.accessExtension = extensionContainer.create("jigsawAccess", AccessExtension.class, target, target.getObjects());
        this.accessExtension.resetManipulators();

        JigsawTransform.PLUGIN_TRANSFORMER_MAP.put(getName(), AccessTransformerASM::new);
    }

    @Override
    public void afterEvaluate(Project project) {
        AccessWriters.initDefaultFormats();
        AccessWriters.MERGED.clear();

        if (!accessExtension.manipulators.get().isEmpty()) {
            FileCollection collection = accessExtension.manipulators.get();

            for (File file : collection) {
                if (file == null) {
                    System.out.println("\u001B[1;91m\t ↳ \u001B[1;0m\u001B[33mFound null value, please remove it from the jigsawAccess block, skipping\u001B[0m");
                    continue;
                }
                if (file.isDirectory()) {
                    System.out.println("\u001B[1;91m\t ↳ \u001B[1;0m\u001B[33mFound directory at \"" + file.getName() + "\", please use file paths, skipping\u001B[0m");
                    continue;
                }
                if (!file.exists()) {
                    System.out.println("\u001B[1;91m\t ↳ \u001B[1;0m\u001B[33mFile at \"" + file.getName() + "\", does not exist, either remove the path or create the file, skipping\u001B[0m");
                    continue;
                }
                if (!file.canRead()) {
                    System.out.println("\u001B[1;91m\t ↳ \u001B[1;0m\u001B[33mFile at \"" + file.getName() + "\", cannot be read, skipping\u001B[0m");
                    continue;
                }

                IWriterFormat format = AccessWriters.getFormat(file.getName());
                if (format == null) {
                    System.out.println("\u001B[1;91m\t ↳ \u001B[1;0m\u001B[33mCould not find manipulation format for file \"" + file.getName() + "\", skipping\u001B[0m");
                    continue;
                }

                try {
                    FileInputStream stream = new FileInputStream(file);
                    byte[] bytes = JavaUtils.readAllBytes(stream);
                    ManipulationFile manipulationFile = format.parse(new String(bytes));
                    System.out.println("\u001B[1;94m\t ↳ \u001B[1;0mAdded file \"" + file.getName() + "\" with format \"" + format.name() + "\" to Global Manipulation File");
                    AccessWriters.MERGED.add(manipulationFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public String getName() {
        return "Jigsaw Access Plugin";
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
