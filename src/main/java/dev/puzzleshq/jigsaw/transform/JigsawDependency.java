package dev.puzzleshq.jigsaw.transform;

import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.component.ComponentIdentifier;
import org.jetbrains.annotations.Nullable;

public class JigsawDependency implements Dependency {

    ComponentIdentifier componentIdentifier;
    Dependency dependency;

    public JigsawDependency(ComponentIdentifier targetComponentId, Dependency source) {
        this.componentIdentifier = targetComponentId;
        this.dependency = source;
    }

    @Override
    public @Nullable String getGroup() {
        return dependency.getGroup();
    }

    @Override
    public String getName() {
        return dependency.getName();
    }

    @Override
    public @Nullable String getVersion() {
        return dependency.getVersion();
    }

    @Override
    public boolean contentEquals(Dependency dependency) {
        return dependency.contentEquals(dependency);
    }

    @Override
    public Dependency copy() {
        return new JigsawDependency(componentIdentifier, dependency.copy());
    }

    @Override
    public @Nullable String getReason() {
        return dependency.getReason();
    }

    @Override
    public void because(@Nullable String reason) {
        dependency.because(reason);
    }
}
