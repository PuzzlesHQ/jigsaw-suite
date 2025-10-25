package dev.puzzleshq.jigsaw.modloader;

import dev.puzzleshq.jigsaw.Plugins;
import dev.puzzleshq.jigsaw.game.JigsawGame;
import dev.puzzleshq.jigsaw.util.AbstractJigsawPlugin;
import dev.puzzleshq.jigsaw.util.ConfigurationUtil;
import dev.puzzleshq.jigsaw.util.JavaUtils;
import org.apache.groovy.json.internal.IO;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class LoaderPlugin extends AbstractJigsawPlugin {

    public static final File MANIFEST_LOCATIONS = new File(Plugins.jigsawDir, "loader/");
    public static final URL PUZZLE_CORE_MANIFEST_URL;

    static {
        try {
            PUZZLE_CORE_MANIFEST_URL = new URL("https://raw.githubusercontent.com/PuzzlesHQ/puzzle-loader-core/refs/heads/versioning/versions.json");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(Project target) {
        super.apply(target);

        target.getConfigurations().register("puzzleLoader").get();
    }

    @Override
    public void afterEvaluate(Project project) {
        super.afterEvaluate(project);

        // I have to do this so it doesn't crash when it can't find it
        project.getConfigurations().all(a -> {
            if (a.getName().equals("puzzleLoader")) {
                /*
                    puzzleLoader("dev.puzzleshq:puzzle-loader-core:$version")
                    puzzleLoader("dev.puzzleshq:puzzle-loader-cosmic:$version")


                    cosmicReach("dev.puzzleshq:puzzle-loader-cosmic:$version")
                 */

                Configuration clientImpl = project.getConfigurations().register("puzzleLoaderClient").get();
                Configuration clientConfig = ConfigurationUtil.getClientConfiguration(project);
                if (clientConfig != null) clientConfig.extendsFrom(clientImpl);

                Configuration commonImpl = project.getConfigurations().register("puzzleLoaderCommon").get();
                Configuration commonConfig = ConfigurationUtil.getCommonConfiguration(project);
                if (commonConfig != null) commonConfig.extendsFrom(commonImpl);

                Configuration serverImpl = project.getConfigurations().register("puzzleLoaderServer").get();
                Configuration serverConfig = ConfigurationUtil.getServerConfiguration(project);
                if (serverConfig != null) serverConfig.extendsFrom(serverImpl);

                DependencyHandler dependencyHandler = project.getDependencies();
                for (Dependency puzzleLoader : project.getConfigurations().getByName("puzzleLoader").getDependencies()) {
                    dependencyHandler.add("puzzleLoaderClient", puzzleLoader.getGroup() + ":" + puzzleLoader.getName() + ":" + puzzleLoader.getVersion() + ":client");

                    dependencyHandler.add("puzzleLoaderClient", puzzleLoader.getGroup() + ":" + puzzleLoader.getName() + ":" + puzzleLoader.getVersion() + ":common");
                    dependencyHandler.add("puzzleLoaderCommon", puzzleLoader.getGroup() + ":" + puzzleLoader.getName() + ":" + puzzleLoader.getVersion() + ":common");
                    dependencyHandler.add("puzzleLoaderServer", puzzleLoader.getGroup() + ":" + puzzleLoader.getName() + ":" + puzzleLoader.getVersion() + ":common");

                    dependencyHandler.add("puzzleLoaderServer", puzzleLoader.getGroup() + ":" + puzzleLoader.getName() + ":" + puzzleLoader.getVersion() + ":server");

                    File manifestFile = new File(MANIFEST_LOCATIONS, puzzleLoader.getName() + "-version-manifest-refreshable.json");
                    byte[] manifestBytes = getOrDownload(manifestFile, "https://raw.githubusercontent.com/PuzzlesHQ/" + puzzleLoader.getName() + "/refs/heads/versioning/versions.json");

                    if (!manifestFile.exists())
                        throw new RuntimeException("Could not find manifest for \"" + puzzleLoader.getName() + "\", This should have been downloaded, there could have been an error or it was deleted.");
                    JsonObject manifestObject = parseBytesToJson(manifestFile, manifestBytes);
                    JsonObject versions = manifestObject.get("versions").asObject();

                    {
                        JsonValue versionObject = versions.get(puzzleLoader.getVersion());
                        if (versionObject == null)
                            throw new RuntimeException(puzzleLoader.getName() + " version \"" + puzzleLoader.getVersion() + "\" does not exist in version manifest!");

                        JsonObject versionObjectJson = versionObject.asObject();

                        File depFile = new File(MANIFEST_LOCATIONS, puzzleLoader.getName() + "-" + puzzleLoader.getVersion() + "-dependencies-refreshable.json");
                        byte[] dependenciesBytes = getOrDownload(depFile, versionObjectJson.get("dependencies").asString());
                        JsonObject dependenciesObject = parseBytesToJson(depFile, dependenciesBytes);

                        processDependenciesObject(
                                project,
                                clientConfig,
                                commonConfig,
                                serverConfig,
                                dependenciesObject
                        );
                    }
                }
            }
        });
    }

    private void processDependenciesObject(Project project, Configuration clientConfig, Configuration commonConfig, Configuration serverConfig, JsonObject dependenciesObject) {
        BiConsumer<String, String> check = (side, configuration) -> {
            JsonArray commonArray = dependenciesObject.get("common").asArray();

            for (JsonValue value : commonArray) {
                JsonObject object = value.asObject();

                String groupId = object.get("groupId").asString();
                String artifactId = object.get("artifactId").asString();
                String version = object.get("version").asString();
                String type = object.get("type").asString();

                if (type.equals("compileOnly")) continue;

                String artifact = groupId + ":" + artifactId + ":" + version;

                if (object.get("classifier") != null) {
                    String classifier = object.get("classifier").asString();

                    artifact += ":" + classifier;
                }

                project.getDependencies().add(configuration, artifact);
            }
        };

        JsonArray array = dependenciesObject.get("repos").asArray();
        for (JsonValue value : array) {
            project.getRepositories().maven(a -> {
                a.setName(value.asObject().get("name").asString());
                try {
                    a.setUrl(new URI(value.asObject().get("url").asString()));
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        if (clientConfig != null) {
            check.accept("client", clientConfig.getName());
            check.accept("common", clientConfig.getName());
        }
        if (commonConfig != null)
            check.accept("common", commonConfig.getName());
        if (serverConfig != null) {
            check.accept("server", serverConfig.getName());
            check.accept("common", serverConfig.getName());
        }
    }

    private JsonObject parseBytesToJson(File manifestFile, byte[] manifestBytes) {
        String contents = new String(manifestBytes);

        JsonValue value = JsonValue.readHjson(contents);
        if (value == null || !value.isObject())
            throw new RuntimeException("Could not parse \"" + manifestFile.getAbsolutePath() + "\", invalid json/hjson format!!");

        return value.asObject();
    }

    private byte[] getOrDownload(File file, String downloadURLString) {
        file.getParentFile().mkdirs();

        byte[] bytes = null;
        try {
            URL downloadURL = new URL(downloadURLString);
            InputStream stream = downloadURL.openStream();
            bytes = JavaUtils.readAllBytes(stream);
            stream.close();
        } catch (IOException ignore) {
        }

        if (bytes != null) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            } catch (IOException e) {
                throw new RuntimeException("Failed to write to file \"" + file + "\"", e);
            }
        }

        if (bytes != null)
            return bytes;

        if (!file.exists())
            throw new RuntimeException("\"" + file.getName() + "\" should have been downloaded, please either connect to internet if you are offline.");

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            bytes = JavaUtils.readAllBytes(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return bytes;
    }

    @Override
    public String getName() {
        return "Jigsaw Loader";
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }
}

