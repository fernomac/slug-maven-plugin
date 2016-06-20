package io.coronet.slug;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A simple abstraction of a file system to allow for easier unit testing.
 */
interface FileSystem {

    /**
     * Checks whether the given file exists.
     *
     * @see File#exists()
     */
    boolean exists(File file);

    /**
     * Creates a directory at the given path, recursively creating parent
     * directories as required.
     *
     * @see File#mkdirs()
     */
    boolean mkdirs(File file);

    /**
     * Creates an {@code OutputStream} that will write to the given file.
     *
     * @see FileOutputStream#FileOutputStream(File)
     */
    OutputStream write(File file) throws IOException;
}
