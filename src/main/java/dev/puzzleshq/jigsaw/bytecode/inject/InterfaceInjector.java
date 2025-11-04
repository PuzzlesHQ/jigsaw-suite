package dev.puzzleshq.jigsaw.bytecode.inject;

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

    public static void search(JsonObject object) {
        JsonValue customValue = object.get("custom");
        if (customValue == null) return;
        JsonValue loomInjectedInterfacesValue = customValue.asObject().get("loom:injected_interfaces");
        if (loomInjectedInterfacesValue == null) return;

        JsonObject loomInjectedInterfacesObject = loomInjectedInterfacesValue.asObject();
        for (JsonObject.Member member : loomInjectedInterfacesObject) {
            Set<String> list = interfaceMap.getOrDefault(member.getName(), new HashSet<>());
            JsonArray array = member.getValue().asArray();
            for (JsonValue value : array) list.add(value.asString());
            interfaceMap.put(member.getName(), list);
        }
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        Set<String> stringInterfaces = new HashSet<>(Arrays.asList(interfaces));
        Set<String> interfaceList = interfaceMap.get(name);
        if (interfaceList != null) stringInterfaces.addAll(interfaceList);

        super.visit(version, access, name, signature, superName, stringInterfaces.toArray(new String[0]));
    }
}
