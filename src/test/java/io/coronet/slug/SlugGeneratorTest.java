package io.coronet.slug;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import org.junit.Assert;
import org.junit.Test;

public class SlugGeneratorTest {

    private byte[] input() {
        return ("/**\n"
              + " * This is a top-level doc comment.\n"
              + " */\n"
              + "slug Test {\n"
              + "    // This is a single-line comment.\n"
              + "\n"
              + "    /*\n"
              + "     * This is a multi-line comment.\n"
              + "     */\n"
              + "\n"
              + "    /**\n"
              + "     * This is a javadoc comment on a field.\n"
              + "     */\n"
              + "    string name;\n"
              + "    int age;\n"
              + "    /**\n"
              + "     * Another javadoc comment for good measure.\n"
              + "     */\n"
              + "    list[map[string]] listOfMaps;\n"
              + "}\n").getBytes(StandardCharsets.UTF_8);
    }

    @Test
    public void testIt() throws Exception {
        TestFileSystem fs = new TestFileSystem();
        SlugGenerator gen = new SlugGenerator(new File("/"), fs);

        InputStream input = new ByteArrayInputStream(input());
        File output = gen.generate("io.coronet.slug", input);

        byte[] result = fs.get(output.getPath());

//        System.out.println(new String(result, StandardCharsets.UTF_8));

        Class<?> c = eval("io.coronet.slug.Test", result);
        Object o = c.newInstance();

        Method getName = c.getMethod("getName");
        Method setName = c.getMethod("setName", String.class);
        Method getAge = c.getMethod("getAge");
        Method setAge = c.getMethod("setAge", Integer.class);

        Assert.assertNull(getName.invoke(o));
        Assert.assertNull(getAge.invoke(o));

        Assert.assertSame(o, setName.invoke(o, "Test"));
        Assert.assertSame(o, setAge.invoke(o, 123));

        Assert.assertEquals("Test", getName.invoke(o));
        Assert.assertEquals(Integer.valueOf(123), getAge.invoke(o));

        Assert.assertNotNull(o.toString());
        o.hashCode();

        Assert.assertTrue(o.equals(o));
        Assert.assertFalse(o.equals(123));

        Object o2 = c.getConstructor(c).newInstance(o);

        Assert.assertEquals(o, o2);
        Assert.assertEquals(o2, o);

        Assert.assertSame(o2, setAge.invoke(o2, 456));
        Assert.assertFalse(o.equals(o2));
        Assert.assertFalse(o2.equals(o));
    }

    private static Class<?> eval(String name, byte[] content) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        SourceFileObject source = new SourceFileObject(name, content);
        ClassFileObject target = new ClassFileObject(name);

        Loader loader = new Loader(target);

        FileManager manager = new FileManager(
                compiler.getStandardFileManager(null, null, null),
                target,
                loader);

        CompilationTask task = compiler.getTask(
                null, manager, null, null, null, Arrays.asList(source));

        if (!task.call()) {
            Assert.fail("Compilation failed");
        }

        try {
            return loader.loadClass(name);
        } catch (ClassNotFoundException e) {
            Assert.fail(e.toString());
            return null;
        }
    }

    private static class FileManager
            extends ForwardingJavaFileManager<JavaFileManager> {

        private final JavaFileObject target;
        private final ClassLoader loader;

        protected FileManager(
                    JavaFileManager fileManager,
                    JavaFileObject target,
                    ClassLoader loader) {

            super(fileManager);
            this.target = target;
            this.loader = loader;
        }

        @Override
        public JavaFileObject getJavaFileForOutput(
                Location location,
                String className,
                Kind kind,
                FileObject sibling) throws IOException {

            return target;
        }

        @Override
        public ClassLoader getClassLoader(Location location) {
            return loader;
        }
    }

    private static class Loader extends ClassLoader {

        private final ClassFileObject obj;

        public Loader(ClassFileObject obj) {
            super(ClassLoader.getSystemClassLoader());
            this.obj = obj;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (obj.getName().equals(name)) {
                byte[] bytes = obj.toByteArray();
                return defineClass(name, bytes, 0, bytes.length);
            }
            return super.findClass(name);
        }
    }

    private static class SourceFileObject extends SimpleJavaFileObject {

        private final byte[] content;

        public SourceFileObject(String name, byte[] content) {
            super(makeUri(name), Kind.SOURCE);
            this.content = content;
        }

        private static URI makeUri(String name) {
            return URI.create("bogus:///" + name.replace('.', '/') + ".java");
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return new String(content, StandardCharsets.UTF_8);
        }
    }

    private static class ClassFileObject extends SimpleJavaFileObject {

        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        public ClassFileObject(String name) {
            super(URI.create(name), Kind.CLASS);
        }

        public byte[] toByteArray() {
            return baos.toByteArray();
        }

        @Override
        public OutputStream openOutputStream() {
            return baos;
        }
    }
}
