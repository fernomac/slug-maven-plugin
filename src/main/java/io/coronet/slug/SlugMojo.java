package io.coronet.slug;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.compiler.util.scan.InclusionScanException;
import org.codehaus.plexus.compiler.util.scan.SimpleSourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * Maven plugin that invokes the {@link SlugGenerator}. Does fancy incremental
 * nonsense so it can run in Eclipse.
 */
@Mojo(
  name = "slug",
  defaultPhase = LifecyclePhase.GENERATE_SOURCES,
  requiresDependencyResolution = ResolutionScope.COMPILE,
  requiresProject = true
)
public class SlugMojo extends AbstractMojo {

    @Component
    private BuildContext build;

    @Parameter(property = "project", required = true, readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "${basedir}/src/main/slug")
    private File sourceDirectory;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/slug")
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException {
        if (!sourceDirectory.isDirectory()) {
            // Nothing to do.
            getLog().error("Source dir not found: " + sourceDirectory);
            return;
        }

        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        SlugGenerator slug = new SlugGenerator(outputDirectory);
        for (File input : findSlugs()) {
            if (build.hasDelta(input)) {
                try {
                    String pkg = getPackage(input);
                    File output = slug.generate(pkg, input);
                    build.refresh(output);
                } catch (IOException e) {
                    throw new MojoExecutionException(e.getMessage(), e);
                }
            }
        }

        project.addCompileSourceRoot(outputDirectory.getPath());
    }

    private Set<File> findSlugs() throws MojoExecutionException {

        SourceInclusionScanner scanner = new SimpleSourceInclusionScanner(
                Collections.singleton("**/*.slug"),
                Collections.emptySet());

        scanner.addSourceMapping(
                new SuffixMapping("slug", Collections.emptySet()));

        try {
             return scanner.getIncludedSources(sourceDirectory, null);
        } catch (InclusionScanException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private String getPackage(File input) {
        String rootp = outputDirectory.getPath();
        String inputp = input.getPath();

        if (!inputp.startsWith(rootp)) {
            throw new IllegalStateException(
                    "input '" + inputp + "' is not a descendant of '"
                    + rootp + "'!");
        }

        if (rootp.endsWith("/")) {
            return inputp.substring(rootp.length());
        } else {
            return inputp.substring(rootp.length() + 1);
        }
    }
}
