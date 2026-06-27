package dev.puzzleshq.jigsaw.bytecode.transform.tasks;

import dev.puzzleshq.jigsaw.bytecode.transform.JarTransformer;
import dev.puzzleshq.jigsaw.bytecode.transform.JigsawTransform;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class CreateTransformSourcesTask extends DefaultTask {

    public CreateTransformSourcesTask() {
        setGroup("jigsaw");
    }

    private static void makeSourceJars(File f) {
        Queue<File> queue = new LinkedList<>();
        queue.add(f);
        while (!queue.isEmpty()) {
            File file = queue.poll();
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    queue.addAll(Arrays.asList(files));
                }
                continue;
            }
            if (file.getName().endsWith(".jar")) {
                JarTransformer.createSourceJar(file);
            }
        }
    }

    @TaskAction
    public void execute() {
        makeSourceJars(JigsawTransform.transformCache);
    }

}
