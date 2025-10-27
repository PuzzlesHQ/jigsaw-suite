package dev.puzzleshq.jigsaw.abstracts;

import dev.puzzleshq.jigsaw.Plugins;
import dev.puzzleshq.jigsaw.StringConstants;
import dev.puzzleshq.jigsaw.gamesupport.game.JigsawGame;
import dev.puzzleshq.jigsaw.util.ConfigurationUtil;
import dev.puzzleshq.jigsaw.bytecode.transform.JarTransformer;
import org.gradle.api.Project;
import org.gradle.api.artifacts.*;
import org.gradle.api.artifacts.dsl.DependencyHandler;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractSplitGamePlugin extends AbstractJigsawPlugin {

    public final String configurationName;
    public final String configurationNameInternal;
    public static File globalJigsawMaven;

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

        Configuration clientImpl = ConfigurationUtil.getClientTransformConfiguration(project);
        Configuration commonImpl = ConfigurationUtil.getCommonTransformConfiguration(project);
        Configuration serverImpl = ConfigurationUtil.getServerTransformConfiguration(project);

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

                    clientNotation.set("global." + dependency.getGroup() + ":" + dependency.getName() + ":" + dependency.getVersion() + ":client");
                    commonNotation.set("global." + dependency.getGroup() + ":" + dependency.getName() + ":" + dependency.getVersion() + ":common");
                    serverNotation.set("global." + dependency.getGroup() + ":" + dependency.getName() + ":" + dependency.getVersion() + ":server");
                    mergedNotation.set("global." + dependency.getGroup() + ":" + dependency.getName() + ":" + dependency.getVersion() + ":merged");

                    clientOut.set(new File(
                            globalJigsawMaven, localPath +
                            "-client.jar"
                    ));

                    commonOut.set(new File(
                            globalJigsawMaven, localPath +
                            "-common.jar"
                    ));

                    serverOut.set(new File(
                            globalJigsawMaven, localPath +
                            "-server.jar"
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

                    dependencyHandler.add(configurationNameInternal, dependencyName + ":client");
                    dependencyHandler.add(configurationNameInternal, dependencyName + ":server");

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
            assert clientImpl != null;
            dependencyHandler.add(clientImpl.getName(), clientNotation.get());
            assert serverImpl != null;
            dependencyHandler.add(serverImpl.getName(), serverNotation.get());
            assert commonImpl != null;
            dependencyHandler.add(commonImpl.getName(), commonNotation.get());
            return;
        }

        if (JigsawGame.IS_SERVER_SPLIT) {
            assert serverImpl != null;
            dependencyHandler.add(serverImpl.getName(), serverNotation.get());
            assert commonImpl != null;
            dependencyHandler.add(commonImpl.getName(), commonNotation.get());
        }

        if (JigsawGame.IS_CLIENT_SPLIT) {
            assert clientImpl != null;
            dependencyHandler.add(clientImpl.getName(), clientNotation.get());
            assert commonImpl != null;
            dependencyHandler.add(commonImpl.getName(), commonNotation.get());
            return;
        }

        if (JigsawGame.IS_MERGED) {
            assert commonImpl != null;
            dependencyHandler.add(commonImpl.getName(), mergedNotation.get());
            return;
        }
    }

    @Override
    public int getPriority() {
        return 6;
    }

}
