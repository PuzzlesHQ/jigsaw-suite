package dev.puzzleshq.jigsaw.publishing.tasks;

import dev.puzzleshq.jigsaw.StringConstants;
import dev.puzzleshq.jigsaw.publishing.Publishing;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.tasks.TaskAction;
import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.hjson.Stringify;

import java.io.File;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DependenciesJson extends DefaultTask {

    public DependenciesJson() {
        setGroup(Publishing.GROUP);
    }

    @TaskAction
    public void exec() throws Exception {
        File file = new File(getProject().getProjectDir(), "dependencies.json");

        JsonObject object = new JsonObject();

        getProject().getConfigurations().all(configuration -> {
            switch (configuration.getName()) {
                case "includedDependency": {
                    add(getProject(), "merged", object, getProject().getConfigurations().getByName("includedDependency"), StringConstants.IMPLEMENTATION_CONFIGURATION);
                    break;
                }
                case "clientIncludedDependency": {
                    add(getProject(), StringConstants.CLIENT_SIDE, object, getProject().getConfigurations().getByName("clientIncludedDependency"), StringConstants.IMPLEMENTATION_CONFIGURATION);
                    break;
                }
                case "serverIncludedDependency": {
                    add(getProject(), StringConstants.SERVER_SIDE, object, getProject().getConfigurations().getByName("serverIncludedDependency"), StringConstants.IMPLEMENTATION_CONFIGURATION);
                    break;
                }
                case "commonIncludedDependency": {
                    add(getProject(), StringConstants.COMMON_SIDE, object, getProject().getConfigurations().getByName("commonIncludedDependency"), StringConstants.IMPLEMENTATION_CONFIGURATION);
                    break;
                }
            }
        });

        JsonArray repos = new JsonArray();
        getProject().getRepositories().forEach(r -> {
            if (r instanceof MavenArtifactRepository) {
                MavenArtifactRepository repo = (MavenArtifactRepository) r;
                if ("MavenLocal".equals(repo.getName())) return;
                JsonObject repoObj = new JsonObject();
                repoObj.add("name", repo.getName());
                repoObj.add("url", repo.getUrl().toString());
                repos.add(repoObj);
            }
        });
        object.add("repos", repos);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(object.toString(Stringify.FORMATTED));
        }
    }

    private static boolean urlExists(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
            connection.setRequestMethod("HEAD");
            connection.setInstanceFollowRedirects(true);
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            int responseCode = connection.getResponseCode();
            connection.disconnect();
            return responseCode >= 200 && responseCode < 400;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static void add(Project project, String side, JsonObject o, Configuration configuration, String type) {
        JsonValue jsonValue = o.get(side);
        JsonArray array;

        if (jsonValue != null){
            array = jsonValue.asArray();
        }else {
            array = new JsonArray();
            o.add(side, array);
        }

        if (!configuration.isCanBeResolved()) return;

        Set<ResolvedArtifact> resolved = configuration.getResolvedConfiguration().getResolvedArtifacts();

        List<MavenArtifactRepository> repos = new ArrayList<>();
        project.getRepositories().forEach(repo -> {
            if (repo instanceof MavenArtifactRepository) repos.add((MavenArtifactRepository) repo);
        });

        // Move Jitpack to the end because it shit :)
        List<MavenArtifactRepository> jitpack = new ArrayList<>();
        repos.removeIf(r -> {
            if ("https://jitpack.io".equalsIgnoreCase(r.getUrl().toString())) {
                jitpack.add(r);
                return true;
            }
            return false;
        });
        repos.addAll(jitpack);

        for (ResolvedArtifact artifact : resolved) {
            JsonObject obj = new JsonObject();

            String group = artifact.getModuleVersion().getId().getGroup();
            String name = artifact.getModuleVersion().getId().getName();
            String version = artifact.getModuleVersion().getId().getVersion();
            String fileName = artifact.getFile().getName();
            String classifier = artifact.getClassifier();

            if (classifier != null) obj.add("classifier", classifier);
            obj.add("groupId", group);
            obj.add("artifactId", name);
            obj.add("version", version);
            obj.add("type", type);

            String groupPath = group.replace('.', '/');
            String relativePath = String.format("%s/%s/%s/%s", groupPath, name, version, fileName);

            String repoUrl = null;
            for (MavenArtifactRepository repo : repos) {
                String base = repo.getUrl().toString().replaceAll("/+$", "");
                String possibleUrl = base + "/" + relativePath;
                if (urlExists(possibleUrl)) {
                    repoUrl = possibleUrl;
                    break;
                }
            }

            if (repoUrl == null) {
                throw new GradleException("Dependency not found in any configured repository: "
                        + group + ":" + name + ":" + version);
            }

            obj.add("artifactUrl", repoUrl);
            array.add(obj);
        }
    }
}
