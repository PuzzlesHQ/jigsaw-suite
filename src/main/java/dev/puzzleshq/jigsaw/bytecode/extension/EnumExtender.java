package dev.puzzleshq.jigsaw.bytecode.extension;

import dev.puzzleshq.jigsaw.bytecode.extension.format.EnumExtensionFormat;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

import java.util.*;

public class EnumExtender {

    public static final Map<String, Set<String>> enumMap = new HashMap<>();

    public static void addEntry(EnumExtensionFormat.EnumsExtensionEntry entry) {
        for (String aClass : entry.getClasses()) {
            Set<String> list = enumMap.getOrDefault(aClass, new HashSet<>());
            list.addAll(entry.getEnums());
            enumMap.put(aClass, list);
        }
    }

    public static void addExtenderFile(String contents) {
        List<EnumExtensionFormat.EnumsExtensionEntry> entries = EnumExtensionFormat.parseInjector(contents);
        for (EnumExtensionFormat.EnumsExtensionEntry entry : entries)
            addEntry(entry);
    }

    public static void search(JsonObject object, Map<String, byte[]> resourceMap) {
        List<String> entries = searchForExtenderEntries(object);

        for (String entry : entries) {
            byte[] bytes = resourceMap.get(entry);
            if (bytes == null) continue;

            addExtenderFile(new String(bytes));
            System.out.println("\u001B[1;94m\t ↳ \u001B[1;0mAdded file \u001B[0;36m\"" + entry + "\"\u001B[0;0m from mod \u001B[0;32m" + object.get("id") + "\u001B[0;0m");
        }
    }

    public static List<String> searchForExtenderEntries(JsonObject object) {
        JsonValue value = object.get("enum-extenders");

        if (value == null) return new ArrayList<>();
        if (value.isString()) return new ArrayList<String>(){{ add(value.asString()); }};

        List<String> strings = new ArrayList<>();
        if (value.isArray()) {
            for (JsonValue jsonValue : value.asArray()) {
                strings.add(jsonValue.asString());
            }
            return strings;
        }

        throw new RuntimeException("`enum-extender` entry must be of type String or Array!");
    }

}
