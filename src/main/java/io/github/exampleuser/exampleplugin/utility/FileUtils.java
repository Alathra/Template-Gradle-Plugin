package io.github.exampleuser.exampleplugin.utility;

import io.github.exampleuser.exampleplugin.ExamplePlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A class that provides a plethora of useful file utilities.
 */
public abstract class FileUtils {
    // Parent

    /**
     * Gets the parent dir of the provided path
     *
     * @param path Path to file or directory
     * @return Parent dir path as string
     */
    public static Path getParentDirPath(@NotNull final Path path) {
        return getParentDirPath(path.toFile()).toPath();
    }

    /**
     * Gets the parent dir of the provided path
     *
     * @param file File representing a file or directory
     * @return Parent dir path as string
     */
    public static File getParentDirPath(@NotNull final File file) {
        return new File(getParentDirPath(file.getAbsolutePath()));
    }

    /**
     * Gets the parent dir of the provided path
     *
     * @param path Path to file or directory
     * @return Parent dir path as string
     */
    public static String getParentDirPath(@NotNull final String path) {
        final boolean endsWithSeparator = path.endsWith(File.separator);
        final int index = endsWithSeparator ? path.length() - 2 : path.length() - 1;

        return path.substring(0, path.lastIndexOf(File.separatorChar, index));
    }

    // File extensions

    /**
     * Get the file extension from the given path
     *
     * @param path a path to a file or directory
     * @return a file extension or "" if none was found
     */
    public static String getExtension(@NotNull final Path path) {
        return getExtension(path.getFileName().toString());
    }

    /**
     * Get the file extension from the given file
     *
     * @param file a file representing a file or directory
     * @return a file extension or "" if none was found
     */
    public static String getExtension(@NotNull final File file) {
        return getExtension(file.getName());
    }

    /**
     * Get the file extension from the given string
     *
     * @param path a string representing a file or directory
     * @return a file extension or "" if none was found
     */
    public static String getExtension(@NotNull final String path) {
        return path.lastIndexOf(".") > 0
            ? path.substring(path.lastIndexOf(".") + 1)
            : "";
    }

    /**
     * Strips file extension from the given path resulting in the file or directory name
     *
     * @param path a path to a file or directory
     * @return a name stripped of file extension
     */
    public static String stripExtension(@NotNull final Path path) {
        return stripExtension(path.getFileName().toString());
    }

    /**
     * Strips file extension from the given file resulting in the file or directory name
     *
     * @param file a file representing a file or directory
     * @return a name stripped of file extension
     */
    public static String stripExtension(@NotNull final File file) {
        return stripExtension(file.getName());
    }

    /**
     * Strips file extension from the given path/file/string resulting in the file or directory name
     *
     * @param fileName a filename
     * @return a name stripped of file extension
     */
    public static String stripExtension(@NotNull final String fileName) {
        if (!fileName.contains(".")) {
            return fileName;
        }

        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    // Resource

    /**
     * Copy a file from the plugin jar to targetPath.
     *
     * @param resourcePath A relative path pointing to a file/directory in the jar
     * @param targetPath   A path pointing to a file/directory in the jar
     * @param overwrite    Whether to overwrite the file if it already exists
     * @throws IOException O
     */
    public static void extractResource(
        @NotNull final Path resourcePath,
        @NotNull final Path targetPath,
        @NotNull final boolean overwrite
    ) throws IOException {
        // Check if path is absolute which breaks our relative path navigation below
        if (resourcePath.isAbsolute())
            throw new IllegalStateException("Directory path is absolute.");

        final File targetDir = getParentDirPath(targetPath.toFile());

        // Ensure target directory exists
        if (!targetDir.exists())
            if (!targetDir.mkdirs())
                throw new IllegalStateException("Failed to create directories.");

        // Overwrite check
        if (targetPath.toFile().exists() && !overwrite)
            return;

        try (
            InputStream stream = ExamplePlugin.getInstance().getResource(resourcePath.toString().replace('\\', '/'))
        ) {
            if (stream == null)
                throw new IllegalStateException("Input stream is null.");

            Files.copy(stream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Returns a list of all files found in a directory located in the plugin jar.
     *
     * @param resourcePath A relative path pointing to a directory in the jar
     * @return A list of all files (excluding directories)
     * @throws URISyntaxException    Thrown when jar cannot be accessed
     * @throws IOException           Thrown when jar cannot be accessed
     * @throws IllegalStateException Misc errors
     */
    public static Set<String> resourceListFiles(
        @NotNull final Path resourcePath
    ) throws URISyntaxException, IOException, IllegalStateException {
        // Check if path is absolute which breaks our relative path navigation below
        if (resourcePath.isAbsolute())
            throw new IllegalStateException("Directory path is absolute.");

        final URL url = ExamplePlugin.getInstance().getClass().getResource("");

        // Check access to the jar
        if (url == null)
            throw new IllegalStateException("Cannot access jar.");

        try (
            final FileSystem fs = FileSystems.newFileSystem(url.toURI(), Collections.emptyMap());
            final Stream<Path> pathStream = Files.list(fs.getRootDirectories().iterator().next().resolve(resourcePath.toString()))
        ) {
            return pathStream
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toSet());
        }
    }
}
