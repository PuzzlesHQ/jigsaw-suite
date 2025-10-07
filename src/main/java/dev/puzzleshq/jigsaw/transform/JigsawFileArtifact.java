package dev.puzzleshq.jigsaw.transform;

import java.io.File;

public class JigsawFileArtifact {

    private final File regularFile;
    private final String configuration;
    private final String notation;
    private final String notation2;
    private final String localPath;

    public JigsawFileArtifact(File regularFile, String configuration, String notation) {
        this.regularFile = regularFile;
        this.configuration = configuration;
        if (notation.contains(" (")) {
            notation = notation.replaceAll("[A-Za-z~`0-9!@#$%^&*.\\- ]*\\(", "");
            notation = notation.replaceAll("\\)", "");
        }
        if (!notation.contains(":")) {
            notation = notation.replace(".jar", "");
            notation = "local-file:" + notation + ":0.0.0@jar";
        }
        this.notation = notation;
        this.notation2 = "transform-cache." + notation;
        if (!notation.contains("@")) notation += "@jar";

        String[] notationSplit = notation.split("@");

        String path;
        String[] strings = notationSplit[0].split(":");
        path = strings[0].replace('.', '/');
        path += "/" + strings[1];
        path += "/" + strings[2];
        path += "/" + strings[1] + "-" + strings[2];


        if (strings.length == 4) {
            path += "-" + strings[0];
        }
        path += "." + notationSplit[1];

        this.localPath = path;
    }

    public String getNotation() {
        return notation;
    }

    public String getNotation2() {
        return notation2;
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
