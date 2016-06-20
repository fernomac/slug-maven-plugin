package io.coronet.slug;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * "Real" implementation of the {@code FileSystem} interface.
 */
final class FileSystemImpl implements FileSystem {

    public FileSystemImpl() {
    }

    @Override
    public boolean exists(File file) {
        return file.exists();
    }

    @Override
    public boolean mkdirs(File file) {
        return file.mkdirs();
    }

    @Override
    public FileOutputStream write(File file) throws IOException {
        return new FileOutputStream(file);
    }
}
