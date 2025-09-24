package dev.puzzleshq.jigsaw;

import dev.puzzleshq.jigsaw.access.JigsawAccess;
import dev.puzzleshq.jigsaw.transform.JigsawTransform;
import dev.puzzleshq.jigsaw.util.AbstractJigsawPlugin;
import org.gradle.api.plugins.PluginContainer;

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

    public static void setup(PluginContainer pluginContainer) {
        if (hasBeenSetup.get()) return;
        hasBeenSetup.set(true);

        jigsawAccessInstalled = pluginContainer.hasPlugin(JigsawAccess.class);
        if (jigsawAccessInstalled) jigsawAccess = pluginContainer.getPlugin(JigsawAccess.class);

        jigsawTransformInstalled = pluginContainer.hasPlugin(JigsawTransform.class);
        if (jigsawTransformInstalled) jigsawTransform = pluginContainer.getPlugin(JigsawTransform.class);
    }

}
