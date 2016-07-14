package io.coronet.slug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.coronet.slug.antlr.SlugParser.DocsContext;
import io.coronet.slug.antlr.SlugParser.FieldContext;
import io.coronet.slug.antlr.SlugParser.SlugContext;

/**
 * Freemarker model type for a slug.
 */
public class SlugModel {

    private final String pkg;
    private final String docs;
    private final String name;
    private final List<FieldModel> fields;

    /**
     * @param pkg the java package for this slug
     * @param context the antlr context to adapt from
     */
    public SlugModel(String pkg, SlugContext context) {
        this(pkg,
                docsFrom(context.docs()),
                context.name().getText(),
                fieldsFrom(context.fields().field()));
    }

    private static String docsFrom(DocsContext docs) {
        return (docs == null || docs.docstring() == null ? null : docs.docstring().getText());
    }

    private static List<FieldModel> fieldsFrom(List<FieldContext> fields) {
        List<FieldModel> result = new ArrayList<>(fields.size());
        for (FieldContext field : fields) {
            result.add(new FieldModel(field));
        }
        return result;
    }

    /**
     * @param pkg the java package for this slug
     * @param docs the docstring for this slug
     * @param name the name of this slug
     * @param fields the field models for this slug
     */
    public SlugModel(
            String pkg,
            String docs,
            String name,
            List<FieldModel> fields) {

        this.pkg = pkg;
        this.docs = docs;
        this.name = name;
        this.fields = fields;
    }

    /**
     * @return the java package for this slug
     */
    public String getPackage() {
        return pkg;
    }

    /**
     * @return the docstring for this slug
     */
    public String getDocs() {
        return docs;
    }

    /**
     * @return the name of this slug
     */
    public String getName() {
        return name;
    }

    /**
     * @return the list of fields for this slug
     */
    public List<FieldModel> getFields() {
        return Collections.unmodifiableList(fields);
    }
}
