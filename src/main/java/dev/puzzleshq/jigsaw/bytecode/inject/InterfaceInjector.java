package dev.puzzleshq.jigsaw.bytecode.inject;

import dev.puzzleshq.jigsaw.bytecode.inject.format.InterfaceInjectorFormat;
import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.util.*;

public class InterfaceInjector extends ClassVisitor {

    public static final Map<String, Set<String>> interfaceMap = new HashMap<>();

    protected InterfaceInjector(ClassVisitor visitor) {
        super(Opcodes.ASM9, visitor);
    }

    public static void addEntry(InterfaceInjectorFormat.InjectionEntry entry) {
        for (String aClass : entry.getClasses()) {
            Set<String> list = interfaceMap.getOrDefault(aClass, new HashSet<>());
            list.addAll(entry.getInterfaces());
            interfaceMap.put(aClass, list);
        }
    }

    public static void addInjectFile(String contents) {
        List<InterfaceInjectorFormat.InjectionEntry> entries = InterfaceInjectorFormat.parseInjector(contents);
        for (InterfaceInjectorFormat.InjectionEntry entry : entries)
            addEntry(entry);
    }

    public static void search(JsonObject object, Map<String, byte[]> resourceMap) {
        List<String> entries = searchForInjectorEntries(object);

        for (String entry : entries) {
            byte[] bytes = resourceMap.get(entry);
            if (bytes == null) continue;

            addInjectFile(new String(bytes));
            System.out.println("\u001B[1;94m\t ↳ \u001B[1;0mAdded file \u001B[0;36m\"" + entry + "\"\u001B[0;0m from mod \u001B[0;32m" + object.get("id") + "\u001B[0;0m");
        }
    }

    private static List<String> searchForInjectorEntries(JsonObject object) {
        JsonValue value = object.get("interface-injectors");

        if (value == null) return new ArrayList<>();
        if (value.isString()) return new ArrayList<String>(){{ add(value.asString()); }};

        List<String> strings = new ArrayList<>();
        if (value.isArray()) {
            for (JsonValue jsonValue : value.asArray()) {
                strings.add(jsonValue.asString());
            }
            return strings;
        }

        throw new RuntimeException("`interfaceInjectors` entry must be of type String or Array!");
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        Set<String> stringInterfaces = new HashSet<>(Arrays.asList(interfaces));
        Set<String> interfaceList = interfaceMap.get(name);
        if (interfaceList != null) stringInterfaces.addAll(interfaceList);

        super.visit(version, access, name, signature, superName, stringInterfaces.toArray(new String[0]));
    }
}
