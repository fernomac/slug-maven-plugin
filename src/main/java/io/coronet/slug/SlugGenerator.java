package io.coronet.slug;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import io.coronet.slug.antlr.SlugLexer;
import io.coronet.slug.antlr.SlugParser;
import io.coronet.slug.antlr.SlugParser.SlugContext;

/**
 * Main slug code generation logic.
 */
public class SlugGenerator {

    private final File root;
    private final FileSystem fs;

    /**
     * @param root the root output directory
     */
    public SlugGenerator(File root) {
        this(root, new FileSystemImpl());
    }

    /**
     * For unit testing.
     */
    SlugGenerator(File root, FileSystem fs) {
        this.root = root;
        this.fs = fs;
    }

    /**
     * Generates a POJO from the given slug description on disk.
     *
     * @param input the file where the slug description can be found
     * @return the file created to hold the generated POJO
     * @throws IOException on error reading or writing
     */
    public File generate(String pkg, File input) throws IOException {
        try (InputStream in = new FileInputStream(input)) {
            return generate(pkg, in);
        }
    }

    /**
     * Generates a POJO from the given slug description provided as
     * an {@code InputStream}.
     *
     * @param pkg the java package to generate into
     * @param in the slug description
     * @return the file created to hold the generated POJO
     * @throws IOException on error reading or writing
     */
    public File generate(String pkg, InputStream in) throws IOException {
        SlugLexer lexer = new SlugLexer(new ANTLRInputStream(in));
        SlugParser parser = new SlugParser(new CommonTokenStream(lexer));

        SlugContext slug = parser.slug();

        return generate(pkg, slug);
    }

    private File generate(String pkg, SlugContext slug) throws IOException {
        File dir = new File(root, pkg.replace('.', File.separatorChar));
        if (!fs.exists(dir)) {
            fs.mkdirs(dir);
        }

        String name = slug.name().getText();
        File file = new File(dir, name + ".java");

        try (OutputStream out = fs.write(file)) {
            Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
            generate(pkg, slug, writer);
        }

        return file;
    }

    private void generate(
            String pkg,
            SlugContext slug,
            Writer out) throws IOException {

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setClassForTemplateLoading(SlugGenerator.class, "");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        SlugModel model = new SlugModel(pkg, slug);
        Template template = cfg.getTemplate("slug.ftl");

        try {
            template.process(model, out);
        } catch (TemplateException e) {
            throw new IllegalStateException(
                    "Error while processing template: " + e.getMessage(),
                    e);
        }
    }
}
