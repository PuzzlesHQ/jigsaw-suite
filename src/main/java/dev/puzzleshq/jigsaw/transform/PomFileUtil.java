package dev.puzzleshq.jigsaw.transform;

import groovy.namespace.QName;
import groovy.util.Node;
import groovy.util.NodeList;
import groovy.xml.XmlParser;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ExcludeRule;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class PomFileUtil {

    private static final XmlParser parser;

    static {
        try {
            parser = new XmlParser();
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void processPom(Map<String, String> dependencyMap, Queue<JigsawFileArtifact> files, String groupId, String artifactId, String versionId, Map<String, String> env, Project project, DependencyHandler dependencyHandler, String configuration, Set<ExcludeRule> exclude, File file) {
        try {
            String pomProjectVersion = "${project.version}";
            Node docNode = parser.parse(file);
            NodeList versionNode = (NodeList) docNode.get("version");
            if (versionNode != null && !versionNode.isEmpty()) {
                pomProjectVersion = versionNode.text();
            }

            String packaging = ((NodeList) docNode.get("packaging")).text();
            if (Objects.equals(packaging, "jar")) {
                String path = groupId.replaceAll("\\.", "/") + "/" + artifactId + "/" + versionId + "/"
                        + artifactId + "-" + versionId + "." + packaging;

                File jarFile = new File(JigsawTransform.jarCache, path);
                jarFile.getParentFile().mkdirs();

                for (ArtifactRepository repository : project.getRepositories()) {
                    if (repository instanceof MavenArtifactRepository) {
                        MavenArtifactRepository mavenArtifactRepository = (MavenArtifactRepository) repository;
                        String urlStr = mavenArtifactRepository.getUrl().toURL().toString();
                        if (!urlStr.endsWith("/")) urlStr += "/";
                        URL url = new URL(urlStr + path);
                        try {
                            InputStream stream = url.openStream();
                            Files.copy(stream, jarFile.toPath().toAbsolutePath(), StandardCopyOption.REPLACE_EXISTING);
                            stream.close();
                            files.add(new JigsawFileArtifact(
                                    jarFile, path, configuration,
                                    groupId, artifactId, versionId
                            ));
                            break;
                        } catch (Exception ignore) {}
                    }
                }
            }

            processParentNode(dependencyMap, files, env, project, docNode.get("parent"), configuration);
            processPropertiesNode(env, docNode.get("properties"));
            processDependenciesNode(dependencyMap, pomProjectVersion, project, configuration, env, docNode.get("dependencies"), exclude);
            processDependencyManagementNode(dependencyMap, pomProjectVersion, project, configuration, env, docNode.get("dependencyManagement"), exclude);
        } catch (IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private static void processDependencyManagementNode(Map<String, String> dependencyMap, String pomProjectVersion, Project project, String configuration, Map<String, String> env, Object management, Set<ExcludeRule> rules) {
        if (management == null || ((NodeList)management).isEmpty()) return;

        NodeList depNode = ((NodeList) management).getAt("dependencies");
        List<Node> list = ((Node)depNode.get(0)).children();
        for (Node node : list) {
            NodeList versionNode = (NodeList) node.get("version");
            if (versionNode == null || versionNode.isEmpty()) continue;
            NodeList scopeNode = (NodeList) node.get("scope");
            if (scopeNode != null && "test".equals(scopeNode.text())) continue;
            NodeList optionalNode = (NodeList) node.get("optional");
            if (optionalNode != null && "true".equals(optionalNode.text())) continue;

            NodeList groupIdNode = (NodeList) node.get("groupId");
            NodeList artifactIdNode = (NodeList) node.get("artifactId");

            String groupId = groupIdNode.text();
            String artifactId = artifactIdNode.text();
            String version = versionNode.text();

            version = version.replaceAll("\\$\\{project\\.version}", pomProjectVersion);
            for (Map.Entry<String, String> stringStringEntry : env.entrySet()) {
                version = version.replaceAll("\\$\\{" + stringStringEntry.getKey() + "}", stringStringEntry.getValue());
            }

            boolean excluded = false;
            for (ExcludeRule rule : rules) {
                if (rule.getGroup().equals(groupId) && rule.getModule().equals(artifactId)) {
                    excluded = true;
                    break;
                }
            }
            if (excluded) continue;
            dependencyMap.put(groupId + ":" + artifactId, version);
        }
    }

    private static final Set<ExcludeRule> emptyExcludeMap = new HashSet<>();

    private static void processParentNode(Map<String, String> dependencyMap, Queue<JigsawFileArtifact> files, Map<String, String> env, Project project, Object parentNode, String configuration) {
        if (parentNode == null || ((NodeList) parentNode).isEmpty()) return;

        NodeList groupIdNode = (NodeList) ((Node)((NodeList) parentNode).get(0)).get("groupId");
        NodeList artifactIdNode = (NodeList) ((Node)((NodeList) parentNode).get(0)).get("artifactId");
        NodeList versionNode = (NodeList) ((Node)((NodeList) parentNode).get(0)).get("version");

        String groupId = groupIdNode.text();
        String artifactId = artifactIdNode.text();
        String version = versionNode.text();

        String artifactIdentifier = groupId + ":" + artifactId + ":" + version;

        System.out.println("\u001B[1;94m\t\t\t ↳ \u001B[1;0m\u001B[37m" + artifactIdentifier + "\u001B[0m");
        DependencyManager.handle(dependencyMap, env, files, project, configuration, groupId, artifactId, version, emptyExcludeMap);
    }

    private static void processDependenciesNode(Map<String, String> dependencyMap, String pomProjectVersion, Project project, String configuration, Map<String, String> env, Object depNode, Set<ExcludeRule> rules) {
        if (depNode == null || ((NodeList) depNode).isEmpty()) return;

        List<Node> list = ((Node)((NodeList) depNode).get(0)).children();
        for (Node node : list) {
            NodeList scopeNode = (NodeList) node.get("scope");
            if (scopeNode != null && "test".equals(scopeNode.text())) continue;
            NodeList optionalNode = (NodeList) node.get("optional");
            if (optionalNode != null && "true".equals(optionalNode.text())) continue;

            NodeList groupIdNode = (NodeList) node.get("groupId");
            NodeList artifactIdNode = (NodeList) node.get("artifactId");

            String groupId = groupIdNode.text();
            String artifactId = artifactIdNode.text();
            String version;

            NodeList versionNode = (NodeList) node.get("version");
            if (versionNode == null || versionNode.isEmpty()) {
                version = dependencyMap.get(groupId + ":" + artifactId);
                if (version == null)
                    continue;
            } else {
                version = versionNode.text();
            }


            version = version.replaceAll("\\$\\{project\\.version}", pomProjectVersion);
            for (Map.Entry<String, String> stringStringEntry : env.entrySet()) {
                version = version.replaceAll("\\$\\{" + stringStringEntry.getKey() + "}", stringStringEntry.getValue());
            }

            boolean excluded = false;
            for (ExcludeRule rule : rules) {
                if (rule.getGroup().equals(groupId) && rule.getModule().equals(artifactId)) {
                    excluded = true;
                    break;
                }
            }
            if (excluded) continue;
            String artifactIdentifier = groupId + ":" + artifactId + ":" + version;
            project.getDependencies().add(configuration, artifactIdentifier);
        }
    }

    private static void processPropertiesNode(Map<String, String> env, Object propertiesNode) {
        if (propertiesNode == null || ((NodeList) propertiesNode).isEmpty()) return;

        List<Node> list = ((Node)((NodeList) propertiesNode).get(0)).children();
        for (Node node : list) {
            String nodeName = ((QName)node.name()).getQualifiedName();
            String contents = node.text();
            env.put(nodeName, contents);
        }
    }

}
