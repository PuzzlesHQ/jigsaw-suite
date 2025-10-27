package dev.puzzleshq.jigsaw.gamesupport.game;

import dev.puzzleshq.jigsaw.gamesupport.game.tasks.run.RunClientTask;
import dev.puzzleshq.jigsaw.gamesupport.game.tasks.run.RunServerTask;
import dev.puzzleshq.jigsaw.gamesupport.game.tasks.run.RunVanillaClientTask;
import dev.puzzleshq.jigsaw.gamesupport.game.tasks.run.RunVanillaServerTask;
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
