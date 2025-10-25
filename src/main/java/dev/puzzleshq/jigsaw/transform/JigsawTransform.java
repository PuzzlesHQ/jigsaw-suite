package dev.puzzleshq.jigsaw.transform;

import dev.puzzleshq.jigsaw.Plugins;
import dev.puzzleshq.jigsaw.util.AbstractJigsawPlugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.*;
import org.gradle.api.artifacts.result.ResolvedArtifactResult;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.internal.hash.Hashing;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class JigsawTransform extends AbstractJigsawPlugin {

    public static final Map<String, BiFunction<AtomicReference<String>, ClassVisitor, ClassVisitor>> PLUGIN_TRANSFORMER_MAP = new HashMap<>();
    public static final Map<String, BiConsumer<String, ClassReader>> PLUGIN_CLASS_PREPROCESSOR_MAP = new HashMap<>();
    public static final Map<String, BiConsumer<String, byte[]>> PLUGIN_RESOURCE_PREPROCESSOR_MAP = new HashMap<>();

    public static final Map<String, Configuration> configurationMap = new HashMap<>();

    public static File jarCache;
    public static File transformCache;
    public static File transformDir;

    @Override
    public void apply(@NotNull Project target) {
        super.apply(target);

        if (jarCache == null) {
            transformDir = new File(Plugins.jigsawDir, "transform");
            jarCache = new File(transformDir, "jar-cache");
            transformCache = new File(transformDir, "transform-cache");
        }
        TransformTasks.registerTasks(target);

        ConfigurationContainer configurations = target.getConfigurations();
        SourceSetContainer sourceSetContainer = target.getExtensions().getByType(SourceSetContainer.class);

        sourceSetContainer.all(sourceSet -> {
            if (sourceSet.getName().equals("main")) {
                Configuration impl = configurations.register("transform").get();
                configurationMap.put("implementation", impl);
                Configuration compile = configurations.register("compileOnlyTransform").get();
                configurationMap.put("compileOnly", compile);
                Configuration runtime = configurations.register("runtimeOnlyTransform").get();
                configurationMap.put("runtimeOnly", runtime);
                return;
            }
            Configuration impl = configurations.register(sourceSet.getName() + "Transform").get();
            configurationMap.put(sourceSet.getName() + "Implementation", impl);
            Configuration compile = configurations.register(sourceSet.getName() + "CompileOnlyTransform").get();
            configurationMap.put(sourceSet.getName() + "CompileOnly", compile);
            Configuration runtime = configurations.register(sourceSet.getName() + "RuntimeOnlyTransform").get();
            configurationMap.put(sourceSet.getName() + "RuntimeOnly", runtime);
        });

        target.getRepositories().maven((r) -> {
            r.setName("Puzzle Maven");
            r.setUrl("https://maven.puzzleshq.dev/releases/");
        });

        target.getRepositories().maven((r) -> {
            r.setName("Maven 2");
            r.setUrl("https://repo.maven.apache.org/maven2/");
        });

        target.getRepositories().ivy(r -> {
            r.setName("Local Jigsaw Repo");
            r.setUrl(transformDir.toURI() + "/");

            r.patternLayout( p -> {
                p.setM2compatible(true);
                p.artifact("[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier])(.[ext])");
            });

            r.metadataSources(s -> {
                s.artifact();
                s.ignoreGradleMetadataRedirection();
            });
        });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void afterEvaluate(Project project) {
        // AnsiColors https://gist.github.com/JBlond/2fea43a3049b38287e5e9cefc87b2124
        for (Map.Entry<String, Configuration> stringConfigurationEntry : JigsawTransform.configurationMap.entrySet()) {
            Configuration configuration = stringConfigurationEntry.getValue();
            DependencySet dependencies = configuration.getAllDependencies();
            if (dependencies.isEmpty()) continue;

            Queue<JigsawFileArtifact> toBeTransformed = new ArrayDeque<>();
            System.out.println("\u001B[1;94m\t ↳ \u001B[1;0mSearching for dependencies using the \u001B[0;32m'" + configuration.getName() + "'\u001B[0;0m configuration");

            for (ResolvedArtifactResult artifact : configuration.getIncoming().getArtifacts()) {
                System.out.println("\u001B[1;95m\t\t ↳ \u001B[1;0m Found artifact \u001B[0;36m'" + artifact + "'\u001B[0;0m");
                String hash = Hashing.sha256().hashBytes(artifact.getFile().getName().getBytes()).toString();
                hash = hash.substring(0, 4) + hash.substring(hash.length() - 4);

                toBeTransformed.add(new JigsawFileArtifact(
                        artifact.getFile(),
                        stringConfigurationEntry.getKey(),
                        artifact.getId().getDisplayName(),
                        hash
                ));
            }

            while (!toBeTransformed.isEmpty()) {
                JigsawFileArtifact artifact = toBeTransformed.remove();

                ((ExternalModuleDependency) Objects.requireNonNull(project.getDependencies()
                        .add(artifact.getConfiguration(), artifact.getNotation2()))).setChanging(true);
            }
        }
    }

    @Override
    public String getName() {
        return "Jigsaw Transform Plugin";
    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE + 1;
    }
}
