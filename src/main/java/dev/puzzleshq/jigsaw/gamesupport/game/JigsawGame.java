package dev.puzzleshq.jigsaw.gamesupport.game;

import dev.puzzleshq.jigsaw.StringConstants;
import dev.puzzleshq.jigsaw.abstracts.AbstractJigsawPlugin;
import dev.puzzleshq.jigsaw.util.GradleUtil;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.initialization.Settings;
import org.gradle.api.initialization.dsl.ScriptHandler;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

import java.io.File;

public class JigsawGame extends AbstractJigsawPlugin {

    public static boolean IS_MERGED = false;
    public static boolean IS_SPLIT = false;
    public static boolean IS_CLIENT_SPLIT = false;
    public static boolean IS_SERVER_SPLIT = false;

    public static SourceSet CLIENT_SOURCE_SET;
    public static SourceSet COMMON_SOURCE_SET;
    public static SourceSet SERVER_SOURCE_SET;

    public static File runDir;

    @Override
    public void apply(Project target) {
        super.apply(target);
        GradleUtil.onSettingsEvaluate(target, settings -> {
            ScriptHandler buildScript = settings.getBuildscript();
            DependencyHandler handler = buildScript.getDependencies();
            handler.add(StringConstants.CLASSPATH, StringConstants.SHADOW_GRADLE_PLUGIN);

            PluginContainer plugins = target.getPlugins();
            plugins.apply(StringConstants.SHADOW_GRADLE_PLUGIN_ID);
        });

        runDir = new File(target.getProjectDir(), "runs");
        runDir.mkdirs();

        target.getExtensions().create("jigsawGame", GameExtension.class, target, target.getObjects());

        ConfigurationContainer configurations = target.getConfigurations();
        SourceSetContainer sourceSetContainer = target.getExtensions().getByType(SourceSetContainer.class);

        sourceSetContainer.all(sourceSet -> {
            if (sourceSet.getName().equals(StringConstants.MAIN)) {
                Configuration impl = configurations.register(StringConstants.BUNDLE_CONFIGURATION).get();
                return;
            }
            Configuration impl = configurations.register(sourceSet.getName() + "Bundle").get();
        });
    }

    @Override
    public void afterEvaluate(Project project) {
        super.afterEvaluate(project);

        GameTasks.registerTasks(project);
    }

    @Override
    public String getName() {
        return "Jigsaw Game Plugin";
    }

    @Override
    public int getPriority() {
        return 4;
    }

}
