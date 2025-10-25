package dev.puzzleshq.jigsaw.game;

import dev.puzzleshq.jigsaw.game.tasks.RunClientTask;
import dev.puzzleshq.jigsaw.game.tasks.RunServerTask;
import dev.puzzleshq.jigsaw.game.tasks.RunVanillaClientTask;
import dev.puzzleshq.jigsaw.game.tasks.RunVanillaServerTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;

public class GameTasks {

    public static void registerTasks(Project project) {
        TaskContainer container = project.getTasks();

        Object value = project.findProperty("game_has_client");
        if (value == null || value.equals("true")) {
            container.register("runModdedClient", RunClientTask.class);
            container.register("runVanillaClient", RunVanillaClientTask.class);
        }

        value = project.findProperty("game_has_server");
        if (value == null || value.equals("true")) {
            container.register("runModdedServer", RunServerTask.class);
            container.register("runVanillaServer", RunVanillaServerTask.class);
        }
    }

}
