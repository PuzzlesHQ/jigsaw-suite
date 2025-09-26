package dev.puzzleshq.jigsaw.transform;

import dev.puzzleshq.jigsaw.Plugins;
import dev.puzzleshq.jigsaw.util.AbstractJigsawPlugin;
import dev.puzzleshq.jigsaw.util.JarTransformer;
import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.*;
import org.gradle.api.artifacts.component.ComponentIdentifier;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.artifacts.repositories.IvyArtifactRepository;
import org.gradle.api.artifacts.repositories.RepositoryContentDescriptor;
import org.gradle.api.internal.artifacts.dependencies.AbstractExternalModuleDependency;
import org.gradle.api.internal.artifacts.dependencies.DefaultFileCollectionDependency;
import org.gradle.api.internal.file.FileCollectionInternal;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.internal.component.external.model.DefaultModuleComponentIdentifier;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassVisitor;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.function.Function;

public class JigsawTransform extends AbstractJigsawPlugin {

    public static final Map<String, Function<ClassVisitor, ClassVisitor>> PLUGIN_TRANSFORMER_MAP = new HashMap<>();

    public TransformExtension transformExtension;
    private static final Map<String, Configuration> configurationMap = new HashMap<>();

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

        ExtensionContainer extensionContainer = target.getExtensions();
        transformExtension = extensionContainer.create("jigsawTransform", TransformExtension.class, target, target.getObjects());

        ConfigurationContainer configurations = target.getConfigurations();
        SourceSetContainer sourceSetContainer = target.getExtensions().getByType(SourceSetContainer.class);

        for (SourceSet sourceSet : sourceSetContainer) {
            if (sourceSet.getName().equals("main")) {
                Configuration configuration = configurations.register("transform").get();
                configurationMap.put("implementation", configuration);
                continue;
            }
            Configuration configuration = configurations.register(sourceSet.getName() + "Transform").get();
            configurationMap.put(sourceSet.getName() + "Implementation", configuration);
        }

        IvyArtifactRepository local = target.getRepositories().ivy(r -> {
            r.setName("Local Jigsaw Repo");
            try {
                r.setUrl(transformDir.toURL() + "/");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            r.patternLayout( p -> {
                p.setM2compatible(true);
                p.artifact("[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier])(.[ext])");
            });

            r.metadataSources(s -> {
                s.artifact();
                s.ignoreGradleMetadataRedirection();
            });
        });
//            target.getRepositories().remove(local);
//            target.getRepositories().add(0, local);

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void afterEvaluate(Project project) {
        // AnsiColors https://gist.github.com/JBlond/2fea43a3049b38287e5e9cefc87b2124
        for (Map.Entry<String, Configuration> stringConfigurationEntry : configurationMap.entrySet()) {
            Configuration configuration = stringConfigurationEntry.getValue();
            DependencySet dependencies = configuration.getAllDependencies();
            if (dependencies.isEmpty()) continue;

            System.out.println("\u001B[1;94m\t ↳ \u001B[1;0mSearching for dependencies using the '" + configuration.getName() + "' configuration");
            Queue<JigsawFileArtifact> toBeTransformed = new ArrayDeque<>();

            Map<String, String> dependencyMap = new HashMap<>();
            Map<String, String> pomEnviornmentMap = new HashMap<>();

            for (Dependency dependency : dependencies) {
                if (dependency instanceof DefaultFileCollectionDependency) {
                    DefaultFileCollectionDependency dependency1 = (DefaultFileCollectionDependency) dependency;
                    DependencyManager.handle(project, dependency1);
                    continue;
                }
                if (dependency instanceof AbstractExternalModuleDependency) {
                    AbstractExternalModuleDependency dependency1 = (AbstractExternalModuleDependency) dependency;

                    DependencyManager.handle(
                            dependencyMap,
                            pomEnviornmentMap,
                            toBeTransformed,
                            project,
                            stringConfigurationEntry.getKey(),
                            dependency1
                    );
                    dependencyMap.clear();
                    pomEnviornmentMap.clear();
                    continue;
                }
            }

            while (!toBeTransformed.isEmpty()) {
                try {
                    JigsawFileArtifact artifact = toBeTransformed.remove();
                    File transformedFile = new File(JigsawTransform.transformCache, artifact.getLocalPath());
                    transformedFile.getParentFile().mkdirs();
                    if (transformedFile.exists())
                        transformedFile.delete();
                    transformedFile.createNewFile();

//                    JarTransformer.process(artifact.getRegularFile(), null);
                    ((ExternalModuleDependency) Objects.requireNonNull(project.getDependencies().add(artifact.getConfiguration(), artifact.getNotation2())))
                            .setChanging(true);
                    JarTransformer.transform(artifact.getRegularFile(), transformedFile, JigsawTransform.PLUGIN_TRANSFORMER_MAP.values());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
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
