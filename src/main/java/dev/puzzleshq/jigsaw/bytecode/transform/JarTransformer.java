package dev.puzzleshq.jigsaw.bytecode.transform;

import dev.puzzleshq.jigsaw.util.JavaUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
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

    public static void split(
            File clientIn, File serverIn,
            File clientOut, File commonOut, File serverOut
    ) throws IOException {
        Map<String, ZipEntry> commonFiles = new HashMap<>();
        Map<String, ZipEntry> serverFiles = new HashMap<>();
        Map<String, ZipEntry> clientFiles = new HashMap<>();

        ZipFile clientJarFile = new ZipFile(clientIn);
        ZipFile serverJarFile = new ZipFile(serverIn);

        Enumeration<? extends ZipEntry> clientJarEntries = clientJarFile.entries();
        Enumeration<? extends ZipEntry> serverJarEntries = serverJarFile.entries();

        while (clientJarEntries.hasMoreElements()) {
            ZipEntry entry = clientJarEntries.nextElement();
            clientFiles.put(entry.getName(), entry);
        }

        while (serverJarEntries.hasMoreElements()) {
            ZipEntry entry = serverJarEntries.nextElement();
            serverFiles.put(entry.getName(), entry);
        }

        Set<Map.Entry<String, ZipEntry>> clientFileEntries = new HashSet<>(clientFiles.entrySet());
        for (Map.Entry<String, ZipEntry> stringZipEntryEntry : clientFileEntries) {
            String fileName = stringZipEntryEntry.getKey();
            ZipEntry fileEntry = stringZipEntryEntry.getValue();

            if (serverFiles.containsKey(stringZipEntryEntry.getKey())) {
                clientFiles.remove(fileName);
                serverFiles.remove(fileName);

                commonFiles.put(fileName, fileEntry);
            }
        }

        serverFiles.remove("META-INF/MANIFEST.MF");
        clientFiles.remove("META-INF/MANIFEST.MF");
        commonFiles.remove("META-INF/MANIFEST.MF");

        FileOutputStream clientOutFileStream = new FileOutputStream(clientOut);
        ZipOutputStream clientOutZipStream = new ZipOutputStream(clientOutFileStream);

        clientOutZipStream.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
        clientOutZipStream.write("Manifest-Version: 1.0".getBytes(StandardCharsets.UTF_8));

        for (ZipEntry value : clientFiles.values()) {
            clientOutZipStream.putNextEntry(new ZipEntry(value.getName()));
            InputStream entryStream = clientJarFile.getInputStream(value);
            byte[] bytes = JavaUtils.readAllBytes(entryStream);
            entryStream.close();
            clientOutZipStream.write(bytes);
        }

        clientOutZipStream.close();
        clientOutFileStream.close();

        FileOutputStream commonOutFileStream = new FileOutputStream(commonOut);
        ZipOutputStream commonOutZipStream = new ZipOutputStream(commonOutFileStream);

        commonOutZipStream.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
        commonOutZipStream.write("Manifest-Version: 1.0".getBytes(StandardCharsets.UTF_8));

        for (ZipEntry value : commonFiles.values()) {
            commonOutZipStream.putNextEntry(new ZipEntry(value.getName()));
            InputStream entryStream = clientJarFile.getInputStream(value);
            byte[] bytes = JavaUtils.readAllBytes(entryStream);
            entryStream.close();
            commonOutZipStream.write(bytes);
        }

        commonOutZipStream.close();
        commonOutFileStream.close();

        FileOutputStream serverOutFileStream = new FileOutputStream(serverOut);
        ZipOutputStream serverOutZipStream = new ZipOutputStream(serverOutFileStream);

        serverOutZipStream.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
        serverOutZipStream.write("Manifest-Version: 1.0".getBytes(StandardCharsets.UTF_8));

        for (ZipEntry value : serverFiles.values()) {
            serverOutZipStream.putNextEntry(new ZipEntry(value.getName()));
            InputStream entryStream = serverJarFile.getInputStream(value);
            byte[] bytes = JavaUtils.readAllBytes(entryStream);
            entryStream.close();
            serverOutZipStream.write(bytes);
        }

        serverOutZipStream.close();
        serverOutFileStream.close();

        clientJarFile.close();
        serverJarFile.close();
    }

    public static void merge(
            File clientIn,
            File serverIn,
            File mergedOut
    ) throws IOException {
        Map<String, ZipEntry> serverFiles = new HashMap<>();
        Map<String, ZipEntry> clientFiles = new HashMap<>();

        ZipFile clientJarFile = new ZipFile(clientIn);
        ZipFile serverJarFile = new ZipFile(serverIn);

        Enumeration<? extends ZipEntry> clientJarEntries = clientJarFile.entries();
        Enumeration<? extends ZipEntry> serverJarEntries = serverJarFile.entries();

        while (clientJarEntries.hasMoreElements()) {
            ZipEntry entry = clientJarEntries.nextElement();
            clientFiles.put(entry.getName(), entry);
        }

        while (serverJarEntries.hasMoreElements()) {
            ZipEntry entry = serverJarEntries.nextElement();
            if (clientFiles.containsKey(entry.getName())) continue;
            serverFiles.put(entry.getName(), entry);
        }

        serverFiles.remove("META-INF/MANIFEST.MF");
        clientFiles.remove("META-INF/MANIFEST.MF");

        FileOutputStream mergedOutFileStream = new FileOutputStream(mergedOut);
        ZipOutputStream mergedOutZipStream = new ZipOutputStream(mergedOutFileStream);

        mergedOutZipStream.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
        mergedOutZipStream.write("Manifest-Version: 1.0".getBytes(StandardCharsets.UTF_8));

        for (ZipEntry value : clientFiles.values()) {
            mergedOutZipStream.putNextEntry(new ZipEntry(value.getName()));
            InputStream entryStream = clientJarFile.getInputStream(value);
            byte[] bytes = JavaUtils.readAllBytes(entryStream);
            entryStream.close();
            mergedOutZipStream.write(bytes);
        }

        for (ZipEntry value : serverFiles.values()) {
            mergedOutZipStream.putNextEntry(new ZipEntry(value.getName()));
            InputStream entryStream = serverJarFile.getInputStream(value);
            byte[] bytes = JavaUtils.readAllBytes(entryStream);
            entryStream.close();
            mergedOutZipStream.write(bytes);
        }

        mergedOutZipStream.close();
        mergedOutFileStream.close();

        clientJarFile.close();
        serverJarFile.close();
    }

}
