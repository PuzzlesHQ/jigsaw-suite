package dev.puzzleshq.jigsaw.util;

import dev.puzzleshq.jigsaw.StringConstants;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.FileCollectionDependency;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PuzzleModJsonsUtil {

    public static Map<JsonObject, Map<String, byte[]>> findModJsons(Project project) {
        Map<JsonObject, Map<String, byte[]>> result = new LinkedHashMap<>();

        List<Configuration> configurations = Stream.of(
                        ConfigurationUtil.getClientConfiguration(project),
                        ConfigurationUtil.getCommonConfiguration(project),
                        ConfigurationUtil.getServerConfiguration(project),
                        ConfigurationUtil.getClientTransformConfiguration(project),
                        ConfigurationUtil.getCommonTransformConfiguration(project),
                        ConfigurationUtil.getServerTransformConfiguration(project)
        )
        .filter(Objects::nonNull)
        .distinct()
        .toList();

        for (Configuration config : configurations) {
            if (config == null || config.getAllDependencies().isEmpty()) continue;

            for (File jar : resolveAllFiles(project, config)) {
                processJar(jar, result);
            }
        }

        return result;
    }

    private static Set<File> resolveAllFiles(Project project, Configuration source) {
        Set<File> files = new LinkedHashSet<>();

        for (Dependency dep : source.getAllDependencies()) {
            if (dep instanceof FileCollectionDependency) {
                ((FileCollectionDependency) dep).getFiles().forEach(files::add);
            }
        }

        if (!source.getAllDependencies().isEmpty()) {
            Configuration resolvable = project.getConfigurations().detachedConfiguration(
                    source.getAllDependencies().toArray(new Dependency[0])
            );
            resolvable.setCanBeResolved(true);
            resolvable.setCanBeConsumed(false);

            try {
                resolvable.getResolvedConfiguration()
                        .getResolvedArtifacts()
                        .forEach(a -> files.add(a.getFile()));
            } catch (Exception ignored) {
            }
        }

        return files;
    }

    private static void processJar(File jar, Map<JsonObject, Map<String, byte[]>> result) {
        if (!jar.exists() || jar.isDirectory() || !jar.getName().endsWith(".jar")) return;

        try (ZipFile zip = new ZipFile(jar)) {
            ZipEntry modEntry = zip.getEntry(StringConstants.PUZZLE_MOD_JSON);
            if (modEntry == null) return;

            JsonObject modJson;
            try (InputStream in = zip.getInputStream(modEntry)) {
                modJson = JsonValue.readHjson(new String(in.readAllBytes(), StandardCharsets.UTF_8)).asObject();
            }

            Map<String, byte[]> resources = new LinkedHashMap<>();
            zip.entries().asIterator().forEachRemaining(entry -> {
                if (entry.isDirectory()) return;
                if (!entry.getName().endsWith(".class")) {
                    try (InputStream in = zip.getInputStream(entry)) {
                        resources.put(entry.getName(), in.readAllBytes());
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to read entry: " + entry.getName(), e);
                    }
                }
            });

            result.put(modJson, resources);

        } catch (IOException e) {
            throw new RuntimeException("Failed to process jar: " + jar.getAbsolutePath(), e);
        }
    }
}
