package dev.puzzleshq.jigsaw.util;

import dev.puzzleshq.jigsaw.game.JigsawGame;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;

public class ConfigurationUtil {

    public static Configuration getClientConfiguration(Project project) {
        Object value = project.findProperty("game_has_client");
        if (value != null && value.equals("false")) return null;

        if (JigsawGame.CLIENT_SOURCE_SET.getName().equals("client")) {
            return project.getConfigurations().getByName("clientImplementation");
        }
        if (JigsawGame.CLIENT_SOURCE_SET.getName().equals("main")) {
            return project.getConfigurations().getByName("implementation");
        }
        return null;
    }

    public static Configuration getServerConfiguration(Project project) {
        Object value = project.findProperty("game_has_server");
        if (value != null && value.equals("false")) return null;

        if (JigsawGame.SERVER_SOURCE_SET.getName().equals("server")) {
            return project.getConfigurations().getByName("serverImplementation");
        }
        if (JigsawGame.SERVER_SOURCE_SET.getName().equals("main")) {
            return project.getConfigurations().getByName("implementation");
        }
        return null;
    }

    public static Configuration getCommonConfiguration(Project project) {
        if (JigsawGame.COMMON_SOURCE_SET.getName().equals("common")) {
            return project.getConfigurations().getByName("commonImplementation");
        }
        if (JigsawGame.COMMON_SOURCE_SET.getName().equals("main")) {
            return project.getConfigurations().getByName("implementation");
        }
        return null;
    }

    public static Configuration getClientTransformConfiguration(Project project) {
        Object value = project.findProperty("game_has_client");
        if (value != null && value.equals("false")) return null;

        if (JigsawGame.CLIENT_SOURCE_SET.getName().equals("client")) {
            return project.getConfigurations().getByName("clientTransform");
        }
        if (JigsawGame.CLIENT_SOURCE_SET.getName().equals("main")) {
            return project.getConfigurations().getByName("implementation");
        }
        return null;
    }

    public static Configuration getServerTransformConfiguration(Project project) {
        Object value = project.findProperty("game_has_server");
        if (value != null && value.equals("false")) return null;

        if (JigsawGame.SERVER_SOURCE_SET.getName().equals("server")) {
            return project.getConfigurations().getByName("serverTransform");
        }
        if (JigsawGame.SERVER_SOURCE_SET.getName().equals("main")) {
            return project.getConfigurations().getByName("transform");
        }
        return null;
    }

    public static Configuration getCommonTransformConfiguration(Project project) {
        if (JigsawGame.COMMON_SOURCE_SET.getName().equals("common")) {
            return project.getConfigurations().getByName("commonTransform");
        }
        if (JigsawGame.COMMON_SOURCE_SET.getName().equals("main")) {
            return project.getConfigurations().getByName("transform");
        }
        return null;
    }

    public static Configuration getClientCompileTransformConfiguration(Project project) {
        Object value = project.findProperty("game_has_client");
        if (value != null && value.equals("false")) return null;

        if (JigsawGame.CLIENT_SOURCE_SET.getName().equals("client")) {
            return project.getConfigurations().getByName("clientCompileOnlyTransform");
        }
        if (JigsawGame.CLIENT_SOURCE_SET.getName().equals("main")) {
            return project.getConfigurations().getByName("compileOnlyTransform");
        }
        return null;
    }

    public static Configuration getServerCompileTransformConfiguration(Project project) {
        Object value = project.findProperty("game_has_server");
        if (value != null && value.equals("false")) return null;

        if (JigsawGame.SERVER_SOURCE_SET.getName().equals("server")) {
            return project.getConfigurations().getByName("serverCompileOnlyTransform");
        }
        if (JigsawGame.SERVER_SOURCE_SET.getName().equals("main")) {
            return project.getConfigurations().getByName("compileOnlyTransform");
        }
        return null;
    }

    public static Configuration getCommonCompileTransformConfiguration(Project project) {
        if (JigsawGame.COMMON_SOURCE_SET.getName().equals("common")) {
            return project.getConfigurations().getByName("commonCompileOnlyTransform");
        }
        if (JigsawGame.COMMON_SOURCE_SET.getName().equals("main")) {
            return project.getConfigurations().getByName("compileOnlyTransform");
        }
        return null;
    }

    public static Configuration getClientRuntimeTransformConfiguration(Project project) {
        Object value = project.findProperty("game_has_client");
        if (value != null && value.equals("false")) return null;

        if (JigsawGame.CLIENT_SOURCE_SET.getName().equals("client")) {
            return project.getConfigurations().getByName("clientRuntimeOnlyTransform");
        }
        if (JigsawGame.CLIENT_SOURCE_SET.getName().equals("main")) {
            return project.getConfigurations().getByName("runtimeOnlyTransform");
        }
        return null;
    }

    public static Configuration getServerRuntimeTransformConfiguration(Project project) {
        Object value = project.findProperty("game_has_server");
        if (value != null && value.equals("false")) return null;

        if (JigsawGame.SERVER_SOURCE_SET.getName().equals("server")) {
            return project.getConfigurations().getByName("serverRuntimeOnlyTransform");
        }
        if (JigsawGame.SERVER_SOURCE_SET.getName().equals("main")) {
            return project.getConfigurations().getByName("runtimeOnlyTransform");
        }
        return null;
    }

    public static Configuration getCommonRuntimeTransformConfiguration(Project project) {
        if (JigsawGame.COMMON_SOURCE_SET.getName().equals("common")) {
            return project.getConfigurations().getByName("commonRuntimeOnlyTransform");
        }
        if (JigsawGame.COMMON_SOURCE_SET.getName().equals("main")) {
            return project.getConfigurations().getByName("runtimeOnlyTransform");
        }
        return null;
    }

}
