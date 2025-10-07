package dev.puzzleshq.jigsaw.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SteamAppLocator {

    private static final String windowsDrives = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static Path locate(int appId) {
        String os = System.getProperty("os.name").toLowerCase();
        Path steamPath = null;

        if (os.contains("win")) {
            for (char drive : windowsDrives.toCharArray()) {
                Path path0 = Paths.get(drive + ":/Program Files (x86)/Steam");
                Path path1 = Paths.get(drive + ":/Program Files/Steam");

                if (Files.exists(path0)) steamPath = path0;
                if (Files.exists(path1)) steamPath = path1;
            }
            if (steamPath == null) return null;
        } else if (os.contains("mac")) {
            steamPath = Paths.get(System.getProperty("user.home"), "Library", "Application Support", "Steam");
        } else {
            // Linux
            steamPath = Paths.get(System.getProperty("user.home"), ".local", "share", "Steam");
            if (!Files.exists(steamPath)) {
                steamPath = Paths.get(System.getProperty("user.home"), ".steam", "steam");
            }
        }

        Path libraryFile = steamPath.resolve("steamapps/libraryfolders.vdf");
        List<Path> libraries = new ArrayList<>();
        libraries.add(steamPath);

        if (Files.exists(libraryFile)) {
            try {
                List<String> lines = Files.readAllLines(libraryFile);
                Pattern pattern = Pattern.compile("\"\\d+\"\\s*\"([^\"]+)\"");
                for (String line : lines) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        libraries.add(Paths.get(matcher.group(1).replace("\\\\", "\\")));
                    }
                }
            } catch (IOException ignored) {}
        }

        Path manifest = steamPath.resolve("steamapps/appmanifest_" + appId + ".acf");
        if (!Files.exists(manifest)) return null;

        try {
            List<String> lines = Files.readAllLines(manifest);
            for (String line : lines) {
                if (line.trim().startsWith("\"installdir\"")) {
                    String gameFolder = line.split("\"")[3];
                    Path gamePath = steamPath.resolve("steamapps/common").resolve(gameFolder);
                    if (Files.exists(gamePath)) {
                        return gamePath.toAbsolutePath();
                    }
                }
            }
        } catch (IOException ignored) {}

        return null;
    }
}