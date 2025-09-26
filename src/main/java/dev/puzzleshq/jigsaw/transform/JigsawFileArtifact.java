package dev.puzzleshq.jigsaw.transform;

import java.io.File;

public class JigsawFileArtifact {

    private final File regularFile;
    private final String localPath;
    private final String configuration;
    private final String notation;
    private final String notation2;

    private final String groupId;
    private final String artifactId;
    private final String version;

    public JigsawFileArtifact(File regularFile, String localPath, String configuration, String groupId, String artifactId, String version) {
        this.regularFile = regularFile;
        this.localPath = localPath;
        this.configuration = configuration;
        this.notation = groupId + ":" + artifactId + ":" + version;
        this.notation2 = "transform-cache." + groupId + ":" + artifactId + ":" + version;

        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getVersion() {
        return version;
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
