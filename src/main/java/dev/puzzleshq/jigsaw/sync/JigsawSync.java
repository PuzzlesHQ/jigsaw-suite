package dev.puzzleshq.jigsaw.sync;

import dev.puzzleshq.jigsaw.Plugins;
import dev.puzzleshq.jigsaw.abstracts.AbstractJigsawPlugin;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;

public class JigsawSync extends AbstractJigsawPlugin {

    static final List<AbstractJigsawPlugin> PLUGIN_LIST = new ArrayList<>();

    @Override
    public void apply(Project target) {
        target.afterEvaluate((p) -> {
            PLUGIN_LIST.clear();
            PLUGIN_LIST.addAll(Plugins.PLUGIN_MAP.values());

            PLUGIN_LIST.sort((a, b) -> {
                int aPriority = a.getPriority();
                int bPriority = b.getPriority();

                return Integer.compare(bPriority, aPriority);
            });

            for (AbstractJigsawPlugin abstractJigsawPlugin : PLUGIN_LIST) {
                System.out.println("Loaded \u001B[0;35m\"" + abstractJigsawPlugin + "\"\u001B[0;0m with Priority: \u001B[0;33m" + abstractJigsawPlugin.getPriority() + "\u001B[0;0m");
                abstractJigsawPlugin.afterEvaluate(p);
            }
        });
    }

    @Override
    public String getName() {
        return "Jigsaw Sync Plugin";
    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }

}
