package dev.puzzleshq.jigsaw.util;

import org.apache.groovy.json.internal.LazyMap;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
public class ZomboidUtil {

    public static void addClasspath(Project project, LazyMap json, Path zomboidPath) {
        List<Object> classpath = (List<Object>) json.get("classpath");
        for (Object file : classpath) {
            if (file.equals(".")) continue;

            Path path = zomboidPath.toAbsolutePath().resolve((String) file);
            ConfigurableFileCollection absPath = project.files(path.toAbsolutePath());

            project.getDependencies().add("clientImplementation", absPath);
            project.getDependencies().add("commonImplementation", absPath);
            project.getDependencies().add("serverImplementation", absPath);
        }
    }

    public static void addJarToDeps(Project project, File jar) {
        if (jar.exists()) {
            ConfigurableFileCollection absPath = project.files(jar.getAbsolutePath());

            project.getDependencies().add("clientCompileOnlyTransform", absPath);
            project.getDependencies().add("commonCompileOnlyTransform", absPath);
            project.getDependencies().add("serverCompileOnlyTransform", absPath);
        }
    }

    public static void convertToJar(Project project, File jar, File dir) throws IOException {
        if (!jar.exists()) {
            jar.createNewFile();

            FileOutputStream fileOutputStream = new FileOutputStream(jar);
            ZipOutputStream stream = new ZipOutputStream(fileOutputStream);
            recurse(stream, "", dir);
            stream.close();
            fileOutputStream.close();
        }
        ConfigurableFileCollection absPath = project.files(jar.getAbsolutePath());

        project.getDependencies().add("clientCompileOnly", absPath);
        project.getDependencies().add("commonCompileOnly", absPath);
        project.getDependencies().add("serverCompileOnly", absPath);
    }

    private static void addEntry(ZipOutputStream zip, String dir, File file) throws IOException {
        zip.putNextEntry(new ZipEntry((dir.isEmpty() ? "" : (dir + "/")) + file.getName()));
        FileInputStream fis = new FileInputStream(file);
        zip.write(JavaUtils.readAllBytes(fis));
        fis.close();
    }

    private static void recurse(ZipOutputStream file, String parent, File d) throws IOException {
        if (d.isDirectory()) {
            for (File f : Objects.requireNonNull(d.listFiles())) {
                if (f.isDirectory()) {
                    recurse(file, (parent.isEmpty() ? "" : (parent + "/")) + f.getName(), f);
                } else {
                    if (!f.getName().contains(".class")) continue;
                    addEntry(file, parent, f);
                }
            }
            return;
        }
        addEntry(file, parent, d);
    }

}
