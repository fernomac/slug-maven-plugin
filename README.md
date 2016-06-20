=Slug

Slug is a maven plugin that generates (intentionally) simple POJOs from a
little schema language. You can think of it like less-fancy version of
[lombok](https://projectlombok.org/) that happens to work exactly the way that
I want it to by default and was way easier to write.

== Using Slug

Add the slug plugin to your project by adding the following to your `pom.xml`:

```xml
  <build>
    <plugins>
      <plugin>
        <groupId>io.coronet.slug</groupId>
        <artifactId>slug-maven-plugin</artifactId>
        <version>${your.favorite.version}</version>
        <executions>
          <execution>
            <goals>
              <goal>slug</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
```

You'll need to check out and `mvn install` slug into your local maven
repository until such time as I decide to push a version into maven
central.

== Slug Definitions

Slugs are defined in `*.slug` files. There's a full grammar
[here](src/main/antlr4), but the basic idea is:

```
/**
 * A Javadoc comment here will be transcribed (more or less) as-is into the
 * generated POJO.
 */
slug Human {

    /**
     * Javadoc comments here will be inserted into the Javadoc blocks
     * for generated getter and setter methods.
     *
     * Valid field types include:
     *
     * primitives
     * ----------
     * - boolean (java.lang.Boolean)
     * - int     (java.lang.Integer)
     * - long    (java.lang.Long)
     * - double  (java.lang.Double)
     * - decimal (java.math.BigDecimal)
     * - string  (java.lang.String)
     *
     * collections
     * -----------
     * - list[T] (java.util.List<T>)
     * - map[T]  (java.util.Map<String, T>)
     *
     * - pretty much anything else (it just gets inserted as-is; I take no
     *   responsibility if you use this for anything other than the names of
     *   other Slug-generated POJOs).
     */
    string name;

    /*
     * Non-javadoc comments (of both standard flavors) are allowed and
     * completely omitted from the generated code.
     */
    list[int] favoriteNumbers;
}
```

Actually, that's pretty much it. Like I said, intentionally simple.

== Generated POJOs

Slug generates a Java class corresponding to each of your slug descriptions.
For each defined field, you get a corresponding field as well as a getter and
(fluent) setter. It also gives you `toString()`, `hashCode()`, and
`equals(Object)` implementations.

```java
public class Human {

    private String _name;
    private List<Integer> _favoriteNumbers;

    public String getName() {
        return this._name;
    }

    public Human setName(String value) {
        this._name = value;
        return this;
    }

    public List<Integer> getFavoriteNumbers() {
        return this._favoriteNumbers;
    }

    public Human setFavoriteNumbers(List<Integer> value) {
        this._favoriteNumbers = value;
        return this;
    }

    // Plus toString(), hashCode(), and equals(Object).
}
```

== Configuring Slug

By default Slug looks for files named `*.slug` under `src/main/slug/` to
process. You can change the source directory by setting the cleverly-
named `sourceDirectory` configuration property in your pom.xml. The
subdirectory structure under this root directory corresponds to the
Java package the generated POJO will live in.

Slug also accepts an `outputDirectory` parameter if you'd like to
change where the generated Java source code ends up. It defaults
to `target/generated-sources/slug/`.

== Slug and IDEs

Slug should work pretty well inside Eclipse/m2e out of the box. There are
almost certainly some rough edges; when in doubt `Project -> Clean...` and you
should be okay. I know pretty much nothing about other IDEs. If you do and
would like to shoot me a pull request that'd be swell.
