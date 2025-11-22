package dev.puzzleshq.jigsaw.sync;

import dev.puzzleshq.jigsaw.Plugins;
import dev.puzzleshq.jigsaw.abstracts.AbstractJigsawPlugin;
import dev.puzzleshq.jigsaw.abstracts.IHashablePlugin;
import org.gradle.api.Project;
import org.gradle.internal.hash.Hashing;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.hjson.Stringify;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class JigsawSync extends AbstractJigsawPlugin {

    static final List<AbstractJigsawPlugin> PLUGIN_LIST = new ArrayList<>();
    static File hashCache;

    @Override
    public void apply(Project project) {

        project.afterEvaluate((p) -> {
            hashCache = new File(Plugins.jigsawDir, "hashCache.json");
            hashCache.getParentFile().mkdirs();

            PLUGIN_LIST.clear();
            PLUGIN_LIST.addAll(Plugins.PLUGIN_MAP.values());

            PLUGIN_LIST.sort((a, b) -> {
                int aPriority = a.getPriority();
                int bPriority = b.getPriority();

                return Integer.compare(bPriority, aPriority);
            });

            for (AbstractJigsawPlugin abstractJigsawPlugin : PLUGIN_LIST) {
                if (abstractJigsawPlugin.equals(this)) continue;
                System.out.println("Loaded \u001B[0;35m\"" + abstractJigsawPlugin + "\"\u001B[0;0m with Priority: \u001B[0;33m" + abstractJigsawPlugin.getPriority() + "\u001B[0;0m");
                abstractJigsawPlugin.afterEvaluate(p);
                if (abstractJigsawPlugin instanceof IHashablePlugin) {
                    IHashablePlugin hashablePlugin = (IHashablePlugin) abstractJigsawPlugin;

                    try {
                        if (!hashCache.exists()) {
                            hashCache.createNewFile();
                        }

                        String oldContent = new String(Files.readAllBytes(hashCache.getAbsoluteFile().toPath()));
                        JsonObject oldObject = new JsonObject();
                        if (!oldContent.isEmpty()) oldObject = JsonValue.readHjson(oldContent).asObject();
                        JsonObject newObject = new JsonObject();

                        boolean fileHashChanged = false;

                        for (File fileToHash : hashablePlugin.getFilesToHash()) {
                            if (fileToHash != null && fileToHash.exists()) {
                                String hashString = Hashing.sha256().hashBytes(Files.readAllBytes(fileToHash.getAbsoluteFile().toPath())).toString();

                                if (oldObject.get(fileToHash.getName()) == null)
                                    fileHashChanged = true;
                                else if (!oldObject.get(fileToHash.getName()).asString().equals(hashString))
                                    fileHashChanged = true;

                                newObject.add(fileToHash.getName(), hashString);
                            }
                        }
                        for (String name : oldObject.names()) {
                            if (newObject.get(name) == null) {
                                newObject.add(name, oldObject.get(name).asString());
                            }
                        }

                        Files.write(hashCache.getAbsoluteFile().toPath(), newObject.toString(Stringify.FORMATTED).getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                        if (fileHashChanged) {
                            System.out.println("Reloading plugin: \u001B[0;35m\"" + abstractJigsawPlugin + "\"\u001B[0;0m");
                            hashablePlugin.triggerChange(project);
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
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
