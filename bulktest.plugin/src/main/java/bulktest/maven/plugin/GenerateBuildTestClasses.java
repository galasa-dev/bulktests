package bulktest.maven.plugin;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

@Mojo(name = "bulkgenerate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class GenerateBuildTestClasses extends AbstractMojo {

    @Component
    private MavenProjectHelper projectHelper;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject       project;

    @Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
    private File               buildDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        File generatedDirectory = new File(this.buildDirectory, "bulktests");
        if (!generatedDirectory.exists()) {
            generatedDirectory.mkdirs();
        } else {
            try {
                FileUtils.cleanDirectory(generatedDirectory);
            } catch (IOException e) {
                throw new MojoExecutionException("Unable to clear the generated directory", e);
            }
        }

        ClassNameManagement classNameManagement = new ClassNameManagement();

        SimpleDelayTests simpleDelayTests = new SimpleDelayTests(classNameManagement, generatedDirectory);

        getLog().info("Generating classes");

        try {
            for(int i = 0; i < 1000; i++) {
                simpleDelayTests.generateClass();
            }
        } catch(Exception e) {
            throw new MojoExecutionException("Unable to generate test classes", e);
        }
        
        
        this.project.addCompileSourceRoot(generatedDirectory.getAbsolutePath());
        
        
        ArrayList<String> testNames = new ArrayList<>(classNameManagement.getClassNames());
        
        StringBuilder sb = new StringBuilder();
        for(String testName : testNames) {
            sb.append("'");
            sb.append(this.project.getName());
            sb.append("/");
            sb.append(testName);
            sb.append("',\n");
        }
        
        Path filelist = Paths.get(buildDirectory.toURI()).resolve("file.list");
        
        try {
            Files.write(filelist, sb.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to write out file list artifact", e);
        }
        
        this.projectHelper.attachArtifact(project, "text", "testlist", filelist.toFile());
        
    }

}
