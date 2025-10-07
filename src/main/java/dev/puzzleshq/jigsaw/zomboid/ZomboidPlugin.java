package dev.puzzleshq.jigsaw.zomboid;

import dev.puzzleshq.jigsaw.util.AbstractJigsawPlugin;
import dev.puzzleshq.jigsaw.util.SteamAppLocator;
import dev.puzzleshq.jigsaw.util.ZomboidUtil;
import groovy.json.JsonSlurper;
import org.apache.groovy.json.internal.LazyMap;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.file.ConfigurableFileCollection;

import java.io.File;
import java.nio.file.Path;

public class ZomboidPlugin extends AbstractJigsawPlugin {

    public static final int steamAppId = 108600;
    public static Path zomboidPath;
    public static File gameJar;
    public static LazyMap json;

    @Override
    public void apply(Project target) {
        super.apply(target);

        target.getExtensions().create("jigsawZomboid", ZomboidExtension.class, target, target.getObjects());
        ZomboidTasks.registerTasks(target);
    }

    @Override
    public void afterEvaluate(Project project) {
        super.afterEvaluate(project);

        zomboidPath = SteamAppLocator.locate(steamAppId);
        if (zomboidPath == null) throw new RuntimeException("You must have Project Zomboid installed via Steam");
        zomboidPath = zomboidPath.toAbsolutePath();

        json = (LazyMap) new JsonSlurper().parse(new File(zomboidPath.toString(), "ProjectZomboid64.json"));

        gameJar = project.file("build/projectZomboid.jar");
        gameJar.getParentFile().mkdirs();

        ZomboidUtil.addJarToDeps(project, gameJar);
        ZomboidUtil.addClasspath(project, json, zomboidPath);

        ConfigurableFileCollection configurableFileCollection = project.files(zomboidPath);

        DependencyHandler dependencyHandler = project.getDependencies();
        dependencyHandler.add("clientRuntimeOnly", configurableFileCollection);
        dependencyHandler.add("serverRuntimeOnly", configurableFileCollection);
        dependencyHandler.add("commonRuntimeOnly", configurableFileCollection);
    }

    @Override
    public String getName() {
        return "Jigsaw Zomboid";
    }

    @Override
    public int getPriority() {
        return 5;
    }
}
