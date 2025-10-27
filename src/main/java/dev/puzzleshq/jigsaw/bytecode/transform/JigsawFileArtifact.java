package dev.puzzleshq.jigsaw.bytecode.transform;

import java.io.File;

public class JigsawFileArtifact {

    private final File regularFile;
    private final String configuration;
    private final String notation;
    private final String transformedNotation;
    private final String localPath;

    public JigsawFileArtifact(File regularFile, String configuration, String notation, boolean isFile) {
        this.regularFile = regularFile;
        this.configuration = configuration;

        if (isFile) {
            notation = notation.replace(".jar", "");
            notation = "local-file:" + notation + ":0.0.0@jar";
        }
        this.notation = notation;
        this.transformedNotation = "cache." + notation;

        if (!notation.contains("@")) notation += "@jar";

        String[] notationSplit = notation.split("@");

        String path;
        String[] strings = notationSplit[0].split(":");
        path = strings[0].replace('.', '/');
        path += "/" + strings[1];
        path += "/" + strings[2];
        path += "/" + strings[1] + "-" + strings[2];

        if (strings.length == 4) {
            path += "-" + strings[strings.length - 1];
        }
        path += "." + notationSplit[1];

        this.localPath = path;
    }

    public String getNotation() {
        return notation;
    }

    public String getTransformedNotation() {
        return transformedNotation;
    }

    public String getConfiguration() {
        return configuration;
    }

    public File getRegularFile() {
        return regularFile;
    }

    public String getLocalPath() {
        return localPath;
    }
}
