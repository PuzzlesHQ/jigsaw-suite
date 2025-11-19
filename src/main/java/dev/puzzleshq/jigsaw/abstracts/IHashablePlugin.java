package dev.puzzleshq.jigsaw.abstracts;

import org.gradle.api.Project;

import java.io.File;

public interface IHashablePlugin {

    File[] getFilesToHash();
    void triggerChange(Project project);

}
