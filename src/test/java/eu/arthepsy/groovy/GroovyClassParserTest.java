package eu.arthepsy.groovy;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class GroovyClassParserTest {


    private Set<String> getClassPaths(String fileClassName) throws IOException {
        return getClassPaths(fileClassName, null);
    }

    private Set<String> getClassPaths(String fileClassName, String packagePath) throws IOException {
        File file = ResourceUtils.getFile(fileClassName + ".groovy");
        assertNotNull(file);
        GroovyClassParser parser = new GroovyClassParser();
        parser.parseGroovyFile(file.getPath());
        Set<String> classPaths = parser.getClassPaths();
        if (packagePath != null && ! packagePath.isEmpty()) {
            assertEquals(classPaths.contains(packagePath + '.' + fileClassName), true);
        } else {
            assertEquals(classPaths.contains(fileClassName), true);
        }
        return classPaths;
    }

    @Test
    public void WithOutClassTest() throws IOException {
        String fileClassName = "WithoutClass";
        Set<String> classPaths = getClassPaths(fileClassName);
        assertEquals(1, classPaths.size());
    }

    @Test
    public void WithoutClassWithPackageTest() throws IOException {
        String fileClassName = "WithoutClassWithPackage";
        String packagePath = "eu.arthepsy.groovy";
        Set<String> classPaths = getClassPaths(fileClassName, packagePath);
        assertEquals(1, classPaths.size());
    }

    @Test
    public void SingleClassAndSameFilenameTest() throws IOException {
        String fileClassName = "SingleClassAndSameFilename";
        Set<String> classPaths = getClassPaths(fileClassName);
        assertEquals(1, classPaths.size());
    }

    @Test
    public void SingleClassAndDifferentFilenameTest() throws IOException {
        String fileClassName = "SingleClassAndDifferentFilename";
        Set<String> classPaths = getClassPaths(fileClassName);
        assertEquals(2, classPaths.size());
        assertEquals(classPaths.contains(fileClassName), true);
    }

    @Test
    public void SingleClassWithPackageTest() throws IOException {
        String fileClassName = "SingleClassWithPackage";
        String packagePath = "eu.arthepsy.groovy";
        Set<String> classPaths = getClassPaths(fileClassName, packagePath);
        assertEquals(1, classPaths.size());
        assertEquals(classPaths.contains(packagePath + '.' + fileClassName), true);
    }

    @Test
    public void SingleNestedClassTest() throws IOException {
        String fileClassName = "SingleNestedClass";
        Set<String> classPaths = getClassPaths(fileClassName);
        assertEquals(4, classPaths.size());
        assertEquals(classPaths.contains("Single"), true);
        assertEquals(classPaths.contains("Single$Nested"), true);
        assertEquals(classPaths.contains("Single$Nested$Clazz"), true);
    }

    @Test
    public void SiblingClassesTest() throws IOException {
        String fileClassName = "SiblingClasses";
        Set<String> classPaths = getClassPaths(fileClassName);
        assertEquals(3, classPaths.size());
        assertEquals(classPaths.contains("Sibling1"), true);
        assertEquals(classPaths.contains("Sibling2"), true);
    }

}
