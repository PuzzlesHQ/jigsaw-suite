package dev.puzzleshq.jigsaw.gamesupport.zomboid;

import dev.puzzleshq.jigsaw.StringConstants;
import dev.puzzleshq.jigsaw.abstracts.AbstractJigsawPlugin;
import dev.puzzleshq.jigsaw.bytecode.transform.JigsawTransform;
import dev.puzzleshq.jigsaw.util.SteamAppLocator;
import groovy.json.JsonSlurper;
import org.apache.groovy.json.internal.LazyMap;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.tasks.JavaExec;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class ZomboidPlugin extends AbstractJigsawPlugin {

    public static final int steamAppId = 108600;
    public static Path zomboidPath;
    public static File gameJar;
    public static LazyMap json;

    @Override
    public void apply(Project target) {
        super.apply(target);

        ZomboidTasks.registerTasks(target);

        target.getTasks().all(a -> {
            if (
                    a.getName().equals("runModdedClient") ||
                    a.getName().equals("runModdedServer") ||
                    a.getName().equals("runVanillaClient") ||
                    a.getName().equals("runVanillaServer")
            ) {
                JavaExec exec = (JavaExec) a;
                exec.jvmArgs((List<String>) ZomboidPlugin.json.get("vmArgs"));
                exec.setWorkingDir(zomboidPath);
            }
        });
    }

    @Override
    public void afterEvaluate(Project project) {
        super.afterEvaluate(project);

        zomboidPath = SteamAppLocator.locate(steamAppId);
        if (zomboidPath == null) throw new RuntimeException("You must have Project Zomboid installed via Steam");
        zomboidPath = zomboidPath.toAbsolutePath();

        json = (LazyMap) new JsonSlurper().parse(new File(zomboidPath.toString(), "ProjectZomboid64.json"));

        gameJar = new File(JigsawTransform.jarCache, "local-file/project-zomboid/0.0.0/project-zomboid-0.0.0.jar");
        gameJar.getParentFile().mkdirs();

        ZomboidUtil.addJarToDeps(project, gameJar);
        ZomboidUtil.addClasspath(project, json, zomboidPath);

        ConfigurableFileCollection configurableFileCollection = project.files(zomboidPath);

        DependencyHandler dependencyHandler = project.getDependencies();

        JigsawTransform.configurationMap.forEach((s, configuration) -> {
            if (s.toLowerCase().contains(StringConstants.RUNTIME_ONLY_CONFIGURATION)) {
                dependencyHandler.add(s, configurableFileCollection);
            }
        });
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
