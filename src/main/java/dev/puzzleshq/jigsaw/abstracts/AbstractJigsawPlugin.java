package dev.puzzleshq.jigsaw.abstracts;

import dev.puzzleshq.jigsaw.Plugins;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public abstract class AbstractJigsawPlugin implements Plugin<Project> {

    protected final String group;

    public AbstractJigsawPlugin() {
        this("");
    }

    public AbstractJigsawPlugin(String group) {
        Plugins.PLUGIN_MAP.put(getName(), this);

        this.group = group.trim().isEmpty() ? "jigsaw" : "jigsaw/" + group;

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

    public String getGroup() {
        return group;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
