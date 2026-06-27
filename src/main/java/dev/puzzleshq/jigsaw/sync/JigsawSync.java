package dev.puzzleshq.jigsaw.sync;

import dev.puzzleshq.jigsaw.Plugins;
import dev.puzzleshq.jigsaw.abstracts.AbstractJigsawPlugin;
import dev.puzzleshq.jigsaw.abstracts.IHashablePlugin;
import org.gradle.api.Project;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.hjson.Stringify;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class JigsawSync extends AbstractJigsawPlugin {

    static final List<AbstractJigsawPlugin> PLUGIN_LIST = new ArrayList<>();
    static File hashCache;
    static FileHashDictionary hashDictionary = new FileHashDictionary();

    @Override
    public void apply(Project project) {
        FileSystem fileSystem = FileSystems.getDefault();
        WatchService watchService = null;
        try {
            watchService = fileSystem.newWatchService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        project.afterEvaluate((p) -> {
            hashCache = new File(Plugins.localJigsawDir, "hashCache.json");
            hashCache.getParentFile().mkdirs();

            PLUGIN_LIST.clear();
            PLUGIN_LIST.addAll(Plugins.PLUGIN_MAP.values());

            PLUGIN_LIST.sort((a, b) -> {
                int aPriority = a.getPriority();
                int bPriority = b.getPriority();

                return Integer.compare(bPriority, aPriority);
            });

            boolean fileHashChanged = false;
            JsonObject oldObject;
            try {
                if (!hashCache.exists()) {
                    hashCache.createNewFile();
                }

                String oldContent = new String(Files.readAllBytes(hashCache.getAbsoluteFile().toPath()));
                if (!oldContent.isEmpty()) {
                    oldObject = JsonValue.readHjson(oldContent).asObject();
                    hashDictionary.fromObject(oldObject);
                } else {
                    fileHashChanged = true;
                    oldObject = new JsonObject();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            for (AbstractJigsawPlugin abstractJigsawPlugin : PLUGIN_LIST) {
                if (abstractJigsawPlugin.equals(this)) continue;
                System.out.println("Loaded \u001B[0;35m\"" + abstractJigsawPlugin + "\"\u001B[0;0m with Priority: \u001B[0;33m" + abstractJigsawPlugin.getPriority() + "\u001B[0;0m");
                abstractJigsawPlugin.afterEvaluate(p);
                if (abstractJigsawPlugin instanceof IHashablePlugin) {
                    IHashablePlugin hashablePlugin = (IHashablePlugin) abstractJigsawPlugin;

                    fileHashChanged |= hashDictionary.addFiles(hashablePlugin.getFilesToHash());
                    fileHashChanged |= hashDictionary.pruneDeletedFiles();
                    fileHashChanged |= hashDictionary.updateHashes();

                    if (fileHashChanged) {
                        System.out.println("Detected Hash Change, Reloading plugin: \u001B[0;35m\"" + abstractJigsawPlugin + "\"\u001B[0;0m");
                        hashablePlugin.triggerChange(project);
                    }
                }
            }

            hashDictionary.toObject(oldObject);
            try {
                Files.write(hashCache.getAbsoluteFile().toPath(), oldObject.toString(Stringify.FORMATTED).getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public String getName() {
        return "Jigsaw Sync Plugin";
    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }

}
