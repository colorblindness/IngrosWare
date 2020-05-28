package best.reich.ingros.util;

import best.reich.ingros.IngrosWare;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * made by Xen for IngrosWare
 * at 1/3/2020
 **/
public class ClassUtil {
    public static List<Class<?>> getClassesIn(String path) {
        final List<Class<?>> classes = new ArrayList<>();
        System.out.println("smh:" + path.replace("e:", ""));
        try {
            final File file = new File(path.replace("e:", ""));
            final ClassLoader classLoader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()}, IngrosWare.class.getClassLoader());
            final ZipFile zip = new ZipFile(file);
            for (Enumeration list = zip.entries(); list.hasMoreElements(); ) {
                final ZipEntry entry = (ZipEntry) list.nextElement();
                if (entry.getName().contains("best/reich/ingros/module/modules/") && entry.getName().contains(".class")) {
                    classes.add(classLoader.loadClass(entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.')));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    public static List<Class<?>> getClassesEx(String path) {
        final List<Class<?>> classes = new ArrayList<>();

        try {
            final File dir = new File(path);

            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (file.getName().endsWith(".jar") || file.getName().endsWith(".zip")) {
                    final ClassLoader classLoader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()}, IngrosWare.class.getClassLoader());
                    final ZipFile zip = new ZipFile(file);
                    for (Enumeration list = zip.entries(); list.hasMoreElements(); ) {
                        final ZipEntry entry = (ZipEntry) list.nextElement();

                        if (entry.getName().contains(".class")) {
                            classes.add(classLoader.loadClass(entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.')));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }
}
