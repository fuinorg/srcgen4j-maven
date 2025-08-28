package org.fuin.srcgen4j.maven;

import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

/**
 * Test for the {@link SrcGen4JMojo} class.
 */
@MavenJupiterExtension
class SrcGen4JMojoTestIT {

    private static final File TEST_DIR = new File("target/maven-it/"
            + SrcGen4JMojoTestIT.class.getPackage().getName().replace(".", "/") + "/"
            + SrcGen4JMojoTestIT.class.getSimpleName()
            + "/testProcessTemplate/project");

    private static final File FILE_A = new File(TEST_DIR, "srcGen/a/A.java");

    private static final File FILE_B = new File(TEST_DIR, "srcGen/b/B.java");

    @BeforeEach
    void setup() {
        if (FILE_A.exists()) {
            assertThat(FILE_A.delete()).isTrue();
            assertThat(FILE_A.getParentFile().delete()).isTrue();
        }
        if (FILE_B.exists()) {
            assertThat(FILE_B.delete()).isTrue();
            assertThat(FILE_B.getParentFile().delete()).isTrue();
        }
    }

    @MavenTest
    @MavenGoal("${project.groupId}:${project.artifactId}:${project.version}:process-template")
    void testProcessTemplate(MavenExecutionResult result) {
        assertThat(result).isSuccessful();
        assertThat(result)
                .out()
                .info()
                .anyMatch(line -> {
                    System.err.println(line);
                    return line.contains("Full parse: parse1");
                })
                .anyMatch(line -> {
                    System.err.println(line);
                    return line.contains("Generation finished: gen1");
                });
        assertThat(FILE_A).exists();
        assertThat(FILE_B).exists();
    }

}
