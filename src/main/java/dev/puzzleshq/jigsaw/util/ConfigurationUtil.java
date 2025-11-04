package dev.puzzleshq.jigsaw.util;

import dev.puzzleshq.jigsaw.StringConstants;
import dev.puzzleshq.jigsaw.gamesupport.game.JigsawGame;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;

public class ConfigurationUtil {

    public static Configuration getClientConfiguration(Project project) {
        Object value = project.findProperty(StringConstants.GAME_HAS_CLIENT);
        if (value != null && value.equals(StringConstants.FALSE)) return null;

        if (JigsawGame.CLIENT_SOURCE_SET.getName().equals(StringConstants.CLIENT_SIDE)) {
            return project.getConfigurations().getByName(StringConstants.CLIENT_IMPLEMENTATION_CONFIGURATION);
        }
        if (JigsawGame.CLIENT_SOURCE_SET.getName().equals(StringConstants.MAIN)) {
            return project.getConfigurations().getByName(StringConstants.IMPLEMENTATION_CONFIGURATION);
        }
        return null;
    }

    public static Configuration getServerConfiguration(Project project) {
        Object value = project.findProperty(StringConstants.GAME_HAS_SERVER);
        if (value != null && value.equals(StringConstants.FALSE)) return null;

        if (JigsawGame.SERVER_SOURCE_SET.getName().equals(StringConstants.SERVER_SIDE)) {
            return project.getConfigurations().getByName(StringConstants.SERVER_IMPLEMENTATION_CONFIGURATION);
        }
        if (JigsawGame.SERVER_SOURCE_SET.getName().equals(StringConstants.MAIN)) {
            return project.getConfigurations().getByName(StringConstants.IMPLEMENTATION_CONFIGURATION);
        }
        return null;
    }

    public static Configuration getCommonConfiguration(Project project) {
        if (JigsawGame.COMMON_SOURCE_SET.getName().equals(StringConstants.COMMON_SIDE)) {
            return project.getConfigurations().getByName(StringConstants.COMMON_IMPLEMENTATION_CONFIGURATION);
        }
        if (JigsawGame.COMMON_SOURCE_SET.getName().equals(StringConstants.MAIN)) {
            return project.getConfigurations().getByName(StringConstants.IMPLEMENTATION_CONFIGURATION);
        }
        return null;
    }

    public static Configuration getClientTransformConfiguration(Project project) {
        Object value = project.findProperty(StringConstants.GAME_HAS_CLIENT);
        if (value != null && value.equals(StringConstants.FALSE)) return null;

        if (JigsawGame.CLIENT_SOURCE_SET.getName().equals(StringConstants.CLIENT_SIDE)) {
            return project.getConfigurations().getByName(StringConstants.CLIENT_TRANSFORM_CONFIGURATION);
        }
        if (JigsawGame.CLIENT_SOURCE_SET.getName().equals(StringConstants.MAIN)) {
            return project.getConfigurations().getByName(StringConstants.IMPLEMENTATION_CONFIGURATION);
        }
        return null;
    }

    public static Configuration getServerTransformConfiguration(Project project) {
        Object value = project.findProperty(StringConstants.GAME_HAS_SERVER);
        if (value != null && value.equals(StringConstants.FALSE)) return null;

        if (JigsawGame.SERVER_SOURCE_SET.getName().equals(StringConstants.SERVER_SIDE)) {
            return project.getConfigurations().getByName(StringConstants.SERVER_TRANSFORM_CONFIGURATION);
        }
        if (JigsawGame.SERVER_SOURCE_SET.getName().equals(StringConstants.MAIN)) {
            return project.getConfigurations().getByName(StringConstants.TRANSFORM_CONFIGURATION);
        }
        return null;
    }

    public static Configuration getCommonTransformConfiguration(Project project) {
        if (JigsawGame.COMMON_SOURCE_SET.getName().equals(StringConstants.COMMON_SIDE)) {
            return project.getConfigurations().getByName(StringConstants.COMMON_TRANSFORM_CONFIGURATION);
        }
        if (JigsawGame.COMMON_SOURCE_SET.getName().equals(StringConstants.MAIN)) {
            return project.getConfigurations().getByName(StringConstants.TRANSFORM_CONFIGURATION);
        }
        return null;
    }

    public static Configuration getClientCompileTransformConfiguration(Project project) {
        Object value = project.findProperty(StringConstants.GAME_HAS_CLIENT);
        if (value != null && value.equals(StringConstants.FALSE)) return null;

        if (JigsawGame.CLIENT_SOURCE_SET.getName().equals(StringConstants.CLIENT_SIDE)) {
            return project.getConfigurations().getByName(StringConstants.CLIENT_COMPILE_ONLY_TRANSFORM_CONFIGURATION);
        }
        if (JigsawGame.CLIENT_SOURCE_SET.getName().equals(StringConstants.MAIN)) {
            return project.getConfigurations().getByName(StringConstants.COMPILE_ONLY_TRANSFORM_CONFIGURATION);
        }
        return null;
    }

    public static Configuration getServerCompileTransformConfiguration(Project project) {
        Object value = project.findProperty(StringConstants.GAME_HAS_SERVER);
        if (value != null && value.equals(StringConstants.FALSE)) return null;

        if (JigsawGame.SERVER_SOURCE_SET.getName().equals(StringConstants.SERVER_SIDE)) {
            return project.getConfigurations().getByName(StringConstants.SERVER_COMPILE_ONLY_TRANSFORM_CONFIGURATION);
        }
        if (JigsawGame.SERVER_SOURCE_SET.getName().equals(StringConstants.MAIN)) {
            return project.getConfigurations().getByName(StringConstants.COMPILE_ONLY_TRANSFORM_CONFIGURATION);
        }
        return null;
    }

    public static Configuration getCommonCompileTransformConfiguration(Project project) {
        if (JigsawGame.COMMON_SOURCE_SET.getName().equals(StringConstants.COMMON_SIDE)) {
            return project.getConfigurations().getByName(StringConstants.COMMON_COMPILE_ONLY_TRANSFORM_CONFIGURATION);
        }
        if (JigsawGame.COMMON_SOURCE_SET.getName().equals(StringConstants.MAIN)) {
            return project.getConfigurations().getByName(StringConstants.COMPILE_ONLY_TRANSFORM_CONFIGURATION);
        }
        return null;
    }

    public static Configuration getClientRuntimeTransformConfiguration(Project project) {
        Object value = project.findProperty(StringConstants.GAME_HAS_CLIENT);
        if (value != null && value.equals(StringConstants.FALSE)) return null;

        if (JigsawGame.CLIENT_SOURCE_SET.getName().equals(StringConstants.CLIENT_SIDE)) {
            return project.getConfigurations().getByName(StringConstants.CLIENT_RUNTIME_ONLY_TRANSFORM_CONFIGURATION);
        }
        if (JigsawGame.CLIENT_SOURCE_SET.getName().equals(StringConstants.MAIN)) {
            return project.getConfigurations().getByName(StringConstants.RUNTIME_ONLY_TRANSFORM_CONFIGURATION);
        }
        return null;
    }

    public static Configuration getServerRuntimeTransformConfiguration(Project project) {
        Object value = project.findProperty(StringConstants.GAME_HAS_SERVER);
        if (value != null && value.equals(StringConstants.FALSE)) return null;

        if (JigsawGame.SERVER_SOURCE_SET.getName().equals(StringConstants.SERVER_SIDE)) {
            return project.getConfigurations().getByName(StringConstants.SERVER_RUNTIME_ONLY_TRANSFORM_CONFIGURATION);
        }
        if (JigsawGame.SERVER_SOURCE_SET.getName().equals(StringConstants.MAIN)) {
            return project.getConfigurations().getByName(StringConstants.RUNTIME_ONLY_TRANSFORM_CONFIGURATION);
        }
        return null;
    }

    public static Configuration getCommonRuntimeTransformConfiguration(Project project) {
        if (JigsawGame.COMMON_SOURCE_SET.getName().equals(StringConstants.COMMON_SIDE)) {
            return project.getConfigurations().getByName(StringConstants.COMMON_RUNTIME_ONLY_TRANSFORM_CONFIGURATION);
        }
        if (JigsawGame.COMMON_SOURCE_SET.getName().equals(StringConstants.MAIN)) {
            return project.getConfigurations().getByName(StringConstants.RUNTIME_ONLY_TRANSFORM_CONFIGURATION);
        }
        return null;
    }

}
