package dev.puzzleshq.jigsaw;

import dev.puzzleshq.jigsaw.access.JigsawAccess;
import dev.puzzleshq.jigsaw.transform.JigsawTransform;
import dev.puzzleshq.jigsaw.util.AbstractJigsawPlugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginContainer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Plugins {

    public static final Map<String, AbstractJigsawPlugin> PLUGIN_MAP = new HashMap<>();

    public static JigsawAccess jigsawAccess;
    public static JigsawTransform jigsawTransform;

    public static boolean jigsawAccessInstalled;
    public static boolean jigsawTransformInstalled;

    private static final AtomicBoolean hasBeenSetup = new AtomicBoolean();

    public static File jigsawDir;
    public static File globalJigsawDir;

    public static void setup(Project project, PluginContainer pluginContainer) {
        if (hasBeenSetup.get()) return;
        hasBeenSetup.set(true);

        jigsawDir = project.file(".gradle/.jigsaw");
        globalJigsawDir = new File(project.getGradle().getGradleUserHomeDir(), "caches/.jigsaw");
        globalJigsawDir.mkdirs();

        jigsawAccessInstalled = pluginContainer.hasPlugin(JigsawAccess.class);
        if (jigsawAccessInstalled) jigsawAccess = pluginContainer.getPlugin(JigsawAccess.class);

        jigsawTransformInstalled = pluginContainer.hasPlugin(JigsawTransform.class);
        if (jigsawTransformInstalled) jigsawTransform = pluginContainer.getPlugin(JigsawTransform.class);
    }

    //jigsawRemap {
    //    registerMappingConfiguration(
    //            "remap1_12_2SRG", // configurationToRemapJars
    //            "mappings.srg:mcp_config:1.12.2-20201025.185735", // mapping
    //            "finalforeach:cosmic-reach:0.5.0-alpha:client" // baseJar

    //remap1_12_2SRG("net.minecraft:client:1.12.2")
    //    mappings "mappings.srg:mcp_config:1.12.2-20201025.185735"
    //mappings "mappings.legacyfabric:yarn:1.8.2+build.551"
    //    mappings "mappings.legacyfabric:yarn:1.8.8+build.551"
    //    )
    //}


}
