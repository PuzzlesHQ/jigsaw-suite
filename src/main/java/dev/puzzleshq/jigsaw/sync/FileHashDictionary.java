package dev.puzzleshq.jigsaw.sync;

import org.gradle.internal.hash.Hashing;
import org.hjson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FileHashDictionary {

    private final Map<File, String> fileToHash;

    public FileHashDictionary() {
        this.fileToHash = new HashMap<>();
    }

    public void fromObject(JsonObject obj) {
        this.fileToHash.clear();
        for (String name : obj.names()) {
            this.fileToHash.put(new File(name), obj.get(name).asString());
        }
    }

    public boolean addFile(File file) {
        if (this.fileToHash.containsKey(file)) {
            return false;
        }
        this.fileToHash.put(file, null);
        return true;
    }

    public boolean addFiles(File[] files) {
        boolean updated = false;
        for (File file : files) {
            if (file == null) continue;
            updated |= addFile(file);
        }
        return updated;
    }

    public boolean pruneDeletedFiles() {
        Set<File> keyset = new HashSet<>(fileToHash.keySet());
        boolean updated = false;
        for (File file : keyset) {
            if (!file.exists()) {
                fileToHash.remove(file);
                updated = true;
            }
        }
        return updated;
    }

    public boolean updateHashes() {
        boolean updated = false;
        for (File file : fileToHash.keySet()) {
            byte[] fileBytes;
            try {
                fileBytes = Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String oldHash = fileToHash.get(file);
            String newHash = Hashing.sha256().hashBytes(fileBytes).toString();

            if (oldHash == null || !oldHash.equals(newHash)) {
                updated = true;
            }

            fileToHash.put(file, newHash);
        }
        return updated;
    }

    public void toObject(JsonObject object) {
        Set<String> names = new HashSet<>(object.names());
        for (String name : names) {
            object.remove(name);
        }

        for (Map.Entry<File, String> entry : fileToHash.entrySet()) {
            object.set(entry.getKey().toPath().toString(), entry.getValue());
        }
    }

}
