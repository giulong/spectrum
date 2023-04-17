package com.giuliolongfils.agitation.internal;

import com.giuliolongfils.agitation.pojos.SystemProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Arrays;

import static java.lang.reflect.Modifier.isFinal;

@Slf4j
public final class Util {

    private static final String HASH_ALGORITHM = "SHA-256";

    private Util() { }

    public static boolean hasSuperclass(final Class<?> child, final Class<?> parent) {
        Class<?> clazz = child.getSuperclass();

        while (clazz != null) {
            if (clazz == parent) {
                return true;
            }
            clazz = clazz.getSuperclass();
        }

        return false;
    }

    @SneakyThrows
    public static byte[] sha256Of(final Path file) {
        final byte[] digest = MessageDigest.getInstance(HASH_ALGORITHM).digest(Files.readAllBytes(file));

        log.debug("{} of file '{}' is '{}'", HASH_ALGORITHM, file.toString(), Arrays.toString(digest));
        return digest;
    }

    public static boolean exists(final Path path) {
        return Files.exists(path) && path.toFile().length() > 0;
    }

    public static boolean isField(final Field f) {
        final int modifiers = f.getModifiers();
        return !f.isSynthetic() && !isFinal(modifiers);
    }

    @SuppressWarnings("findsecbugs:PATH_TRAVERSAL_IN")
    public static String getReportPath(final SystemProperties systemProperties) {
        return Paths.get(systemProperties.getReportsFolder(), systemProperties.getReportName()).toString().replaceAll("\\\\", "/");
    }
}
