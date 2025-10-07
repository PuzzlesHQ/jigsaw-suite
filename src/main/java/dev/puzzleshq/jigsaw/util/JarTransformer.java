package dev.puzzleshq.jigsaw.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.*;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class JarTransformer {

    public static void process(
            File in,
            Collection<BiConsumer<String, ClassReader>> classProcessorCollection,
            Collection<BiConsumer<String, byte[]>> resourceProcessorCollection
    ) {
        try {
            FileInputStream stream = new FileInputStream(in);
            byte[] bytes = JavaUtils.readAllBytes(stream);
            stream.close();

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ZipInputStream zipInputStream = new ZipInputStream(byteArrayInputStream);

            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                byte[] entryBytes = JavaUtils.readAllBytes(zipInputStream);
                if (entry.getName().endsWith(".class")) {
                    ClassReader classReader = new ClassReader(entryBytes);
                    String className = classReader.getClassName();

                    for (BiConsumer<String, ClassReader> value : classProcessorCollection) {
                        value.accept(className, classReader);
                    }
                } else {
                    for (BiConsumer<String, byte[]> value : resourceProcessorCollection) {
                        value.accept(entry.getName(), entryBytes);
                    }
                }
            }

            byteArrayInputStream.close();
            zipInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void transform(File in, File out, Collection<BiFunction<AtomicReference<String>, ClassVisitor, ClassVisitor>> transformerCollection) {
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
                    String className = classReader.getClassName();

                    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                    ClassVisitor last = writer;

                    AtomicReference<String> atomicReference = new AtomicReference<>(className);
                    for (BiFunction<AtomicReference<String>, ClassVisitor, ClassVisitor> value : transformerCollection) {
                        last = value.apply(atomicReference, last);
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
            zipOutputStream.finish();
            zipOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
