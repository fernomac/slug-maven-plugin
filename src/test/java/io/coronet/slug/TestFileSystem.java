package io.coronet.slug;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class TestFileSystem implements FileSystem {

    private final Map<String, ByteArrayOutputStream> files = new HashMap<>();

    public byte[] get(String path) {
        ByteArrayOutputStream baos = files.get(path);
        if (baos == null) {
            return null;
        }
        return baos.toByteArray();
    }

    @Override
    public boolean exists(File file) {
        return false;
    }

    @Override
    public boolean mkdirs(File file) {
        return true;
    }

    @Override
    public OutputStream write(File file) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        files.put(file.getPath(), baos);
        return baos;
    }
}
