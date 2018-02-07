package multithreading.triggers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TriggerLoader extends ClassLoader {
    private final String JAR_PATH = "/Users/Ilnar/IdeaProjects/ipj/out/artifacts/ipj_jar/ipj.jar";
    private final HashMap<String, Class<?>> cache = new HashMap<>();

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            return super.findSystemClass(name);
        } catch (ClassNotFoundException ignored) {
        }
        if (cache.containsKey(name)) {
            return cache.get(name);
        }

        byte[] bytes;
        try (JarFile jarLib = new JarFile(JAR_PATH)) {
            String fileName = name.replace('.', File.separatorChar) + ".class";
            JarEntry jarEntry = jarLib.getJarEntry(fileName);
            InputStream jis = jarLib.getInputStream(jarEntry);
            bytes = jis.readAllBytes();
            if (bytes.length != jarEntry.getSize()) {
                throw new IOException("Read less than jarEntrySize");
            }
        } catch (IOException e) {
            throw new ClassNotFoundException(e.getMessage(), e);
        }
        cache.put(name, defineClass(name, bytes, 0, bytes.length));
        return cache.get(name);
    }

    public Trigger loadTrigger(String name) throws ClassNotFoundException {
        try {
            return (Trigger) loadClass(name).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
