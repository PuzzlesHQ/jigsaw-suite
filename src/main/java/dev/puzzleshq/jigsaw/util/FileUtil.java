package dev.puzzleshq.jigsaw.util;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileUtil {

    public static void delete(File f) {
        if (!f.exists()) return;

        Queue<File> queue = new ArrayDeque<>();
        queue.add(f);

        while (!queue.isEmpty()) {
            File branch = queue.poll();
            if (!branch.exists()) continue;

            if (branch.isDirectory()) {
                File[] files = branch.listFiles();
                assert files != null;
                if (files.length == 0) {
                    branch.delete();
                    continue;
                }
                queue.addAll(Arrays.asList(files));
                queue.add(branch);
                continue;
            }
            branch.delete();
        }
    }

}
