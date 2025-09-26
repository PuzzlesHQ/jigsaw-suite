package dev.puzzleshq.jigsaw.transform;

import org.gradle.api.Project;
import org.gradle.api.artifacts.ExcludeRule;
import org.gradle.api.artifacts.query.ArtifactResolutionQuery;
import org.gradle.api.artifacts.result.*;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.artifacts.dependencies.AbstractExternalModuleDependency;
import org.gradle.api.internal.artifacts.dependencies.DefaultFileCollectionDependency;
import org.gradle.maven.MavenModule;
import org.gradle.maven.MavenPomArtifact;

import java.io.File;
import java.util.*;

public class DependencyManager {

    public static void handle(Project project, DefaultFileCollectionDependency dependency) {
        FileCollection collection = dependency.getFiles();
        for (File file : collection) {
            System.out.println("\u001B[1;94m\t\t ↳ \u001B[1;0m\u001B[36m'" + file + "'\u001B[0m");
        }
    }

    public static void handle(Map<String, String> dependencyMap, Map<String, String> env, Queue<JigsawFileArtifact> files, Project project, String configuration, String groupId, String artifactId, String versionId, Set<ExcludeRule> excludeRules) {
        ArtifactResolutionQuery query = project.getDependencies().createArtifactResolutionQuery();
        query.forModule(groupId, artifactId, versionId);

        //noinspection unchecked
        query.withArtifacts(MavenModule.class, MavenPomArtifact.class);

        Queue<ResolvedArtifactResult> resultsToProcess = new ArrayDeque<>();
        ArtifactResolutionResult result = query.execute();
        for (ComponentResult component : result.getComponents()) {
            if (component instanceof UnresolvedComponentResult) {
                System.out.println("\u001B[1;91m\t\t ↳ \u001B[1;0m\u001B[33m" + groupId + ":" + artifactId + ":" + versionId + "\u001B[0m");
                UnresolvedComponentResult unresolvedComponentResult = (UnresolvedComponentResult) component;
                System.out.println("\t\t\t\u001B[31m" + unresolvedComponentResult.getFailure().toString().replaceAll("\n", "\n\t\t\t") + "\u001B[0m");
                continue;
            }
            if (component instanceof ComponentArtifactsResult) {
                ComponentArtifactsResult componentResult = (ComponentArtifactsResult) component;
                System.out.println("\u001B[1;91m\t\t ↳ \u001B[1;0m\u001B[36m" + groupId + ":" + artifactId + ":" + versionId + "\u001B[0m");
                Set<ArtifactResult> artifactResults = componentResult.getArtifacts(MavenPomArtifact.class);
                for (ArtifactResult artifact : artifactResults) {
                    ResolvedArtifactResult resolvedArtifactResult = (ResolvedArtifactResult) artifact;
                    resultsToProcess.add(resolvedArtifactResult);
                }

                continue;
            }
            System.out.println("Unhandled component result type \"" + component.getClass() + "\"");
        }

        while (!resultsToProcess.isEmpty()) {
            ResolvedArtifactResult resolvedArtifactResult = resultsToProcess.remove();
            File file = resolvedArtifactResult.getFile();
            PomFileUtil.processPom(
                    dependencyMap,
                    files,
                    groupId, artifactId, versionId,
                    env, project, project.getDependencies(),
                    configuration, excludeRules, file
            );
        }
    }

    public static void handle(Map<String, String> dependencyMap, Map<String, String> pomEnv, Queue<JigsawFileArtifact> files, Project project, String configuration, AbstractExternalModuleDependency dependency) {
        handle(dependencyMap, pomEnv, files, project, configuration, dependency.getGroup(), dependency.getName(), Objects.requireNonNull(dependency.getVersion()), dependency.getExcludeRules());
    }

}
