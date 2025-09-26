package dev.puzzleshq.jigsaw.util;

import dev.puzzleshq.jigsaw.Plugins;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public abstract class AbstractJigsawPlugin implements Plugin<Project> {

    public AbstractJigsawPlugin() {
        Plugins.PLUGIN_MAP.put(getName(), this);

        for (AbstractJigsawPlugin value : Plugins.PLUGIN_MAP.values()) {
            if (value == this) continue;
            value.onJigsawPluginInstalled(this);
        }
    }

    @Override
    public void apply(Project target) {
        Plugins.setup(target, target.getPlugins());
    }

    public void onJigsawPluginInstalled(AbstractJigsawPlugin plugin) {}

    public void afterEvaluate(Project project) {}

    public abstract String getName();
    public abstract int getPriority();

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
