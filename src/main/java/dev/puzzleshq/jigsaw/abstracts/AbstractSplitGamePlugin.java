package dev.puzzleshq.jigsaw.abstracts;

import dev.puzzleshq.jigsaw.Plugins;
import dev.puzzleshq.jigsaw.StringConstants;
import dev.puzzleshq.jigsaw.bytecode.transform.JarTransformer;
import dev.puzzleshq.jigsaw.bytecode.transform.JigsawTransform;
import dev.puzzleshq.jigsaw.gamesupport.game.JigsawGame;
import dev.puzzleshq.jigsaw.util.ConfigurationUtil;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedConfiguration;
import org.gradle.api.artifacts.dsl.DependencyHandler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractSplitGamePlugin extends AbstractJigsawPlugin implements IHashablePlugin {

    public final String configurationName;
    public final String configurationNameInternal;
    public static File globalJigsawMaven;
    public static File dependencyNameFile;

    public AbstractSplitGamePlugin(String configurationName) {
        this.configurationName = configurationName;
        this.configurationNameInternal = configurationName + "_internal";
    }

    @Override
    public void apply(Project target) {
        super.apply(target);

        target.getConfigurations().register(configurationName).get();
        target.getConfigurations().register(configurationNameInternal).get();

        globalJigsawMaven = new File(Plugins.globalJigsawDir, "maven");
        globalJigsawMaven.mkdirs();

        dependencyNameFile = new File(Plugins.localJigsawDir, "game/" + configurationName + "Version.txt");
        dependencyNameFile.getParentFile().mkdirs();
        try {
            dependencyNameFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        target.getRepositories().ivy(r -> {
            r.setName("Global Jigsaw Repo");
            r.setUrl(globalJigsawMaven.toURI() + "/");

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

    @Override
    public void afterEvaluate(Project project) {
        super.afterEvaluate(project);

        Configuration clientRuntime = ConfigurationUtil.getClientTransformConfiguration(project);
        Configuration commonRuntime = ConfigurationUtil.getCommonTransformConfiguration(project);
        Configuration serverRuntime = ConfigurationUtil.getServerTransformConfiguration(project);

        AtomicReference<File> clientIn = new AtomicReference<>();
        AtomicReference<File> serverIn = new AtomicReference<>();

        AtomicReference<File> clientOut = new AtomicReference<>();
        AtomicReference<File> commonOut = new AtomicReference<>();
        AtomicReference<File> serverOut = new AtomicReference<>();
        AtomicReference<File> mergedOut = new AtomicReference<>();

        AtomicReference<String> clientNotation = new AtomicReference<>();
        AtomicReference<String> commonNotation = new AtomicReference<>();
        AtomicReference<String> serverNotation = new AtomicReference<>();
        AtomicReference<String> mergedNotation = new AtomicReference<>();

        DependencyHandler dependencyHandler = project.getDependencies();
        project.getConfigurations().all(configuration -> { // I have to do this so it doesn't crash when it can't find it
            if (configuration.getName().equals(configurationNameInternal)) {
                for (Dependency dependency : configuration.getDependencies()) {
                    String localPath = "global/" + Objects.requireNonNull(dependency.getGroup()).replaceAll("//.", "/");
                    localPath += "/" + dependency.getName();
                    localPath += "/" + dependency.getVersion();

                    File baseDir = new File(globalJigsawMaven, localPath);
                    baseDir.mkdirs();

                    localPath += "/" + dependency.getName() + "-" + dependency.getVersion();

                    clientNotation.set("global." + dependency.getGroup() + ":" + dependency.getName() + ":" + dependency.getVersion() + ":" + StringConstants.CLIENT_SIDE);
                    commonNotation.set("global." + dependency.getGroup() + ":" + dependency.getName() + ":" + dependency.getVersion() + ":" + StringConstants.COMMON_SIDE);
                    serverNotation.set("global." + dependency.getGroup() + ":" + dependency.getName() + ":" + dependency.getVersion() + ":" + StringConstants.SERVER_SIDE);
                    mergedNotation.set("global." + dependency.getGroup() + ":" + dependency.getName() + ":" + dependency.getVersion() + ":merged");

                    clientOut.set(new File(
                            globalJigsawMaven, localPath +
                            "-" + StringConstants.CLIENT_SIDE +".jar"
                    ));

                    commonOut.set(new File(
                            globalJigsawMaven, localPath +
                            "-" + StringConstants.COMMON_SIDE + ".jar"
                    ));

                    serverOut.set(new File(
                            globalJigsawMaven, localPath +
                            "-" + StringConstants.SERVER_SIDE + ".jar"
                    ));

                    mergedOut.set(new File(
                            globalJigsawMaven, localPath +
                            "-merged.jar"
                    ));
                }

                ResolvedConfiguration resolvedConfiguration = configuration.getResolvedConfiguration();
                Set<ResolvedArtifact> resolvedArtifacts = resolvedConfiguration.getResolvedArtifacts();

                for (ResolvedArtifact resolvedArtifact : resolvedArtifacts) {
                    String classifier = resolvedArtifact.getClassifier();

                    if (StringConstants.SERVER_SIDE.equals(classifier)) {
                        serverIn.set(resolvedArtifact.getFile());
                        continue;
                    }

                    if (StringConstants.CLIENT_SIDE.equals(classifier)) {
                        clientIn.set(resolvedArtifact.getFile());
                    }

                }

            }

            if (configuration.getName().equals(configurationName)) {
                for (Dependency dependency : configuration.getDependencies()) {
                    String dependencyName = dependency.getGroup() + ":" + dependency.getName() + ":" + dependency.getVersion();

                    try {
                        Files.write(dependencyNameFile.getAbsoluteFile().toPath(), dependencyName.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    dependencyHandler.add(configurationNameInternal, dependencyName + ":" + StringConstants.CLIENT_SIDE);
                    dependencyHandler.add(configurationNameInternal, dependencyName + ":" + StringConstants.SERVER_SIDE);
                }
            }
        });

        try {
            if (!mergedOut.get().exists())
                JarTransformer.merge(clientIn.get(), serverIn.get(), mergedOut.get());
            if (!clientOut.get().exists() || !commonOut.get().exists() || !serverOut.get().exists())
                JarTransformer.split(clientIn.get(), serverIn.get(), clientOut.get(), commonOut.get(), serverOut.get());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (JigsawGame.IS_SPLIT) {
            assert clientRuntime != null;
            dependencyHandler.add(clientRuntime.getName(), clientNotation.get());
            dependencyHandler.add(clientRuntime.getName(), commonNotation.get());
            assert serverRuntime != null;
            dependencyHandler.add(serverRuntime.getName(), serverNotation.get());
            dependencyHandler.add(serverRuntime.getName(), commonNotation.get());
            assert commonRuntime != null;
            dependencyHandler.add(commonRuntime.getName(), commonNotation.get());
            return;
        }

        if (JigsawGame.IS_SERVER_SPLIT) {
            assert serverRuntime != null;
            dependencyHandler.add(serverRuntime.getName(), serverNotation.get());
            dependencyHandler.add(serverRuntime.getName(), commonNotation.get());
            assert commonRuntime != null;
            dependencyHandler.add(commonRuntime.getName(), commonNotation.get());
        }

        if (JigsawGame.IS_CLIENT_SPLIT) {
            assert clientRuntime != null;
            dependencyHandler.add(clientRuntime.getName(), clientNotation.get());
            dependencyHandler.add(clientRuntime.getName(), commonNotation.get());
            assert commonRuntime != null;
            dependencyHandler.add(commonRuntime.getName(), commonNotation.get());
            return;
        }

        if (JigsawGame.IS_MERGED) {
            assert commonRuntime != null;
            dependencyHandler.add(commonRuntime.getName(), mergedNotation.get());
            return;
        }
    }

    @Override
    public int getPriority() {
        return 6;
    }

    @Override
    public File[] getFilesToHash() {
        return new File[]{dependencyNameFile};
    }

    @Override
    public void triggerChange(Project project) {
        if (Plugins.jigsawTransform != null) {
            JigsawTransform.autoTransform(project);
        }
    }
}
