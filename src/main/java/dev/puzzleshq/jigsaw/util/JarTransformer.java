package dev.puzzleshq.jigsaw.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.*;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class JarTransformer {

    public static void process(File in, Collection<Consumer<ClassReader>> transformerCollection) {
        try {
            FileInputStream stream = new FileInputStream(in);
            byte[] bytes = JavaUtils.readAllBytes(stream);
            stream.close();

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ZipInputStream zipInputStream = new ZipInputStream(byteArrayInputStream);

            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().endsWith(".class")) {
                    byte[] entryBytes = JavaUtils.readAllBytes(zipInputStream);
                    ClassReader classReader = new ClassReader(entryBytes);

                    for (Consumer<ClassReader> value : transformerCollection) {
                        value.accept(classReader);
                    }
                }
            }

            byteArrayInputStream.close();
            zipInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void transform(File in, File out, Collection<Function<ClassVisitor, ClassVisitor>> transformerCollection) {
        System.out.println(transformerCollection.size());
        try {
            FileInputStream stream = new FileInputStream(in);
            byte[] bytes = JavaUtils.readAllBytes(stream);
            stream.close();

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ZipInputStream zipInputStream = new ZipInputStream(byteArrayInputStream);

            FileOutputStream fileOutputStream = new FileOutputStream(out);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                byte[] entryBytes = JavaUtils.readAllBytes(zipInputStream);
                if (entry.getName().endsWith(".class")) {
                    ClassReader classReader = new ClassReader(entryBytes);
                    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                    ClassVisitor last = writer;

                    for (Function<ClassVisitor, ClassVisitor> value : transformerCollection) {
                        last = value.apply(last);
                    }

                    classReader.accept(last, 0);

                    byte[] newBytes = writer.toByteArray();
                    zipOutputStream.putNextEntry(entry);
                    zipOutputStream.write(newBytes);
                } else {
                    zipOutputStream.putNextEntry(entry);
                    zipOutputStream.write(entryBytes);
                }
            }

            byteArrayInputStream.close();
            zipInputStream.close();
            zipOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
