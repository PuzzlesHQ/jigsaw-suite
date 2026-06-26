package dev.puzzleshq.jigsaw.bytecode.extension;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EnumClassVisitor extends ClassVisitor {

    String name = null;
    Type currentType;
    private final List<FieldNode> existingConstants = new ArrayList<>();
    private final List<Runnable> postVisitTasks = new ArrayList<>();

    protected EnumClassVisitor(ClassVisitor visitor) {
        super(Opcodes.ASM9, visitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if ((access & Opcodes.ACC_ENUM) != 0 &&EnumExtender.enumMap.containsKey(name)){
            this.name = name;
            this.currentType = Type.getObjectType(name);
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        FieldNode node = new FieldNode(access, name, descriptor, signature, value);

        if ((access & Opcodes.ACC_ENUM) != 0) {
            existingConstants.add(node);
        } else {
            postVisitTasks.add(() -> node.accept(cv));
        }

        return node;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodNode node = new MethodNode(access, name, descriptor, signature, exceptions);
        postVisitTasks.add(() -> node.accept(cv));
        return node;
    }

    static final int ACCESS = Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_STATIC | Opcodes.ACC_ENUM;

    @Override
    public void visitEnd() {
        if (cv == null) {
            return;
        }

        for (FieldNode existingConstant : existingConstants) {
            existingConstant.accept(cv);
        }

        if (name != null) {
            Set<String > enums = EnumExtender.enumMap.get(this.name);
            for (String name : enums) {
                FieldVisitor visitor = super.visitField(ACCESS, name, currentType.getDescriptor(), null, null);
                visitor.visitAttribute(new FakeEnumConstantAttribute());
                visitor.visitEnd();
            }
            name = null;
        }

        for (Runnable task : postVisitTasks) {
            task.run();
        }

        super.visitEnd();
    }

    private static final class FakeEnumConstantAttribute extends Attribute {
        FakeEnumConstantAttribute() {
            super("org.spongepowered.asm.mixin.StubEnumConstant");
        }

        @Override
        protected ByteVector write(ClassWriter classWriter, byte[] code, int codeLength, int maxStack, int maxLocals) {
            return new ByteVector();
        }
    }


}
