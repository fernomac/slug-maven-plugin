package io.coronet.slug;

import java.util.Locale;

import io.coronet.slug.antlr.SlugParser.DocsContext;
import io.coronet.slug.antlr.SlugParser.FieldContext;

/**
 * FreeMarker model type for a field.
 */
public class FieldModel {

    private final String docs;
    private final String name;
    private final String type;

    /**
     * @param context the antlr context to adapt from
     */
    public FieldModel(FieldContext context) {
        this(docsFrom(context.docs()),
                context.name().getText(),
                context.type().getText());
    }

    private static String docsFrom(DocsContext context) {
        return (context == null ? null : context.docstring().getText());
    }

    /**
     * @param docs the docstring for this field, if any
     * @param name the name of this field
     * @param type the type of this field
     */
    public FieldModel(String docs, String name, String type) {
        this.docs = docs;
        this.name = name;
        this.type = type;
    }

    /**
     * @return the doc string for this field
     */
    public String getDocs() {
        return docs;
    }

    /**
     * @return the name of this field
     */
    public String getName() {
        return name;
    }

    /**
     * @return the capitalized name of this field, for method names
     */
    public String getCapitalizedName() {
        return name.substring(0, 1).toUpperCase(Locale.ENGLISH)
                .concat(name.substring(1));
    }

    /**
     * @return the slug type of this field
     */
    public String getType() {
        return type;
    }

    /**
     * @return the java type to use for this field
     */
    public String getJavaType() {
        return getJavaType(type);
    }

    private static String getJavaType(String type) {
        if (type.startsWith("list[")) {
            if (!type.endsWith("]")) {
                throw new IllegalStateException("Bogus type: '" + type + "'");
            }
            return "java.util.List<"
                    + getJavaType(type.substring(5, type.length() - 1))
                    + ">";
        }

        if (type.startsWith("map[")) {
            if (!type.endsWith("]")) {
                throw new IllegalStateException("Bogus type: '" + type + "'");
            }
            return "java.util.Map<String, "
                    + getJavaType(type.substring(4, type.length() - 1))
                    + ">";
        }

        switch (type) {
        case "boolean":   return "Boolean";
        case "int":       return "Integer";
        case "long":      return "Long";
        case "double":    return "Double";
        case "decimal":   return "java.math.BigDecimal";
        case "string":    return "String";
        default:          return type;
        }
    }
}
