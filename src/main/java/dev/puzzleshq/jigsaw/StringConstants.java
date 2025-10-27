package dev.puzzleshq.jigsaw;

public class StringConstants {

    public static final String PUZZLE_MOD_JSON = "puzzle.mod.json";

    public static final String CLIENT_SIDE = "client";
    public static final String COMMON_SIDE = "common";
    public static final String SERVER_SIDE = "server";

    public static final String RUNTIME_ONLY_CONFIGURATION = "runtimeOnly";

    public static final String RUNTIME_ONLY_TRANSFORM_CONFIGURATION = "runtimeOnlyTransform";
    public static final String CLIENT_RUNTIME_ONLY_TRANSFORM_CONFIGURATION = "clientRuntimeOnlyTransform";
    public static final String COMMON_RUNTIME_ONLY_TRANSFORM_CONFIGURATION = "commonRuntimeOnlyTransform";
    public static final String SERVER_RUNTIME_ONLY_TRANSFORM_CONFIGURATION = "serverRuntimeOnlyTransform";

    public static final String COMPILE_ONLY_CONFIGURATION = "compileOnly";

    public static final String COMPILE_ONLY_TRANSFORM_CONFIGURATION = "compileOnlyTransform";
    public static final String CLIENT_COMPILE_ONLY_TRANSFORM_CONFIGURATION = "clientCompileOnlyTransform";
    public static final String COMMON_COMPILE_ONLY_TRANSFORM_CONFIGURATION = "commonCompileOnlyTransform";
    public static final String SERVER_COMPILE_ONLY_TRANSFORM_CONFIGURATION = "serverCompileOnlyTransform";

    public static final String IMPLEMENTATION_CONFIGURATION = "implementation";
    public static final String CLIENT_IMPLEMENTATION_CONFIGURATION = "clientImplementation";
    public static final String COMMON_IMPLEMENTATION_CONFIGURATION = "commonImplementation";
    public static final String SERVER_IMPLEMENTATION_CONFIGURATION = "serverImplementation";

    public static final String TRANSFORM_CONFIGURATION = "transform";
    public static final String CLIENT_TRANSFORM_CONFIGURATION = "clientTransform";
    public static final String COMMON_TRANSFORM_CONFIGURATION = "commonTransform";
    public static final String SERVER_TRANSFORM_CONFIGURATION = "serverTransform";

    public static final String BUNDLE_CONFIGURATION = "bundle";
    public static final String CLIENT_BUNDLE_CONFIGURATION = "clientBundle";
    public static final String COMMON_BUNDLE_CONFIGURATION = "commonBundle";
    public static final String SERVER_BUNDLE_CONFIGURATION = "serverBundle";

    // quick constants gen
    public static void main(String[] args) {
        String[] sides = {"client", "common", "server"};
        String[] configurations = {"implementation", "transform", "bundle"};
        String[] configurations2 = {"runtime Only", "compile Only"};

        for (String side : sides) {
            System.out.println("public static final String " + side.toUpperCase() + "_SIDE = \"" + side + "\";");
        }

        for (String configuration : configurations2) {
            System.out.println("\npublic static final String " + configuration.replaceAll(" ", "_").toUpperCase() + "_CONFIGURATION = \"" + configuration.replaceAll(" ", "") + "\";");
            System.out.println("\npublic static final String " + configuration.replaceAll(" ", "_").toUpperCase() + "_TRANSFORM_CONFIGURATION = \"" + configuration.replaceAll(" ", "") + "Transform\";");

            String base = configuration.substring(0, 1).toUpperCase() + configuration.substring(1);
            for (String side : sides) {
                System.out.println("public static final String " +
                        side.toUpperCase() + "_"
                        + configuration.replaceAll(" ", "_").toUpperCase()
                        + "_TRANSFORM_CONFIGURATION = \"" + side + base.replaceAll(" ", "") + "Transform\";"
                );
            }

        }

        for (String configuration : configurations) {
            System.out.println("\npublic static final String " + configuration.replaceAll(" ", "_").toUpperCase() + "_CONFIGURATION = \"" + configuration.replaceAll(" ", "") + "\";");

            String base = configuration.substring(0, 1).toUpperCase() + configuration.substring(1);
            for (String side : sides) {
                System.out.println("public static final String " +
                        side.toUpperCase() + "_"
                        + configuration.replaceAll(" ", "_").toUpperCase()
                        + "_CONFIGURATION = \"" + side + base.replaceAll(" ", "") + "\";"
                );
            }

        }
    }

}
