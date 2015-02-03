/**
 * The MIT License
 * Copyright (c) 2015 Andris Raugulis
 * moo@arthepsy.eu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package eu.arthepsy.groovy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.Set;

import org.codehaus.groovy.tools.shell.IO;
import org.codehaus.groovy.tools.shell.util.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class GroovyClassParserTest {

    private ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private ByteArrayOutputStream errStream = new ByteArrayOutputStream();

    private GroovyClassParser parseFile(String fileClassName) throws IOException {
        File file = ResourceUtils.getFile(fileClassName + ".groovy");
        assertNotNull(file);
        GroovyClassParser parser = new GroovyClassParser();
        parser.parseGroovyFile(file.getPath());
        return parser;
    }

    private Set<String> getClassPaths(String fileClassName) throws IOException {
        return getClassPaths(fileClassName, null);
    }

    private Set<String> getClassPaths(String fileClassName, String packagePath) throws IOException {
        GroovyClassParser parser = parseFile(fileClassName);
        Set<String> classPaths = parser.getClassPaths();
        if (packagePath != null && ! packagePath.isEmpty()) {
            assertTrue(classPaths.contains(packagePath + '.' + fileClassName));
        } else {
            assertTrue(classPaths.contains(fileClassName));
        }
        return classPaths;
    }

    @Before
    public void initStreams() throws IllegalAccessException {
        Logger.io = new IO(System.in, outStream, errStream);
    }

    @After
    public void resetStreams() {
        outStream.reset();
        errStream.reset();
    }

    @Test
    public void WithOutClassTest() throws IOException {
        String fileClassName = "WithoutClass";
        Set<String> classPaths = getClassPaths(fileClassName);
        assertEquals(1, classPaths.size());
    }

    @Test
    public void WithoutClassWithSimplePackageTest() throws IOException {
        String fileClassName = "WithoutClassWithSimplePackage";
        String packagePath = "groovy";
        Set<String> classPaths = getClassPaths(fileClassName, packagePath);
        assertEquals(1, classPaths.size());
    }

    @Test
    public void WithoutClassWithNestedPackageTest() throws IOException {
        String fileClassName = "WithoutClassWithNestedPackage";
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
        assertTrue(classPaths.contains(fileClassName));
    }

    @Test
    public void SingleClassWithPackageTest() throws IOException {
        String fileClassName = "SingleClassWithPackage";
        String packagePath = "eu.arthepsy.groovy";
        Set<String> classPaths = getClassPaths(fileClassName, packagePath);
        assertEquals(1, classPaths.size());
        assertTrue(classPaths.contains(packagePath + '.' + fileClassName));
    }

    @Test
    public void SingleNestedClassTest() throws IOException {
        String fileClassName = "SingleNestedClass";
        Set<String> classPaths = getClassPaths(fileClassName);
        assertEquals(4, classPaths.size());
        assertTrue(classPaths.contains("Single"));
        assertTrue(classPaths.contains("Single$Nested"));
        assertTrue(classPaths.contains("Single$Nested$Clazz"));
    }

    @Test
    public void SiblingClassesTest() throws IOException {
        String fileClassName = "SiblingClasses";
        Set<String> classPaths = getClassPaths(fileClassName);
        assertEquals(3, classPaths.size());
        assertTrue(classPaths.contains("Sibling1"));
        assertTrue(classPaths.contains("Sibling2"));
    }

    @Test
    public void SingleInterfaceTest() throws IOException {
        String fileClassName = "SingleInterface";
        Set<String> classPaths = getClassPaths(fileClassName);
        assertEquals(2, classPaths.size());
        assertTrue(classPaths.contains("SimpleInterface"));
    }

    @Test
    public void SingleEnumTest() throws IOException {
        String fileClassName = "SingleEnum";
        Set<String> classPaths = getClassPaths(fileClassName);
        assertEquals(2, classPaths.size());
        assertTrue(classPaths.contains("SimpleEnum"));
    }

    @Test
    public void SingleAnnotationTest() throws IOException {
        String fileClassName = "SingleAnnotation";
        Set<String> classPaths = getClassPaths(fileClassName);
        assertEquals(3, classPaths.size());
        assertTrue(classPaths.contains("SimpleFeature"));
        assertTrue(classPaths.contains("SimpleClass"));
    }

    @Test
    public void SingleTraitTest() throws IOException {
        String fileClassName = "SingleTrait";
        Set<String> classPaths = getClassPaths(fileClassName);
        assertEquals(2, classPaths.size());
        assertTrue(classPaths.contains("SimpleTrait"));
    }

    @Test
    public void RecognitionExceptionTest() throws IOException {
        String fileClassName = "RecognitionException";
        parseFile(fileClassName);
        String out = outStream.toString();
        assertTrue(out.contains(" ignored due to RecognitionException: "));
    }

    @Test
    public void TokenStreamExceptionTest() throws IOException {
        String fileClassName = "TokenStreamException";
        parseFile(fileClassName);
        String out = outStream.toString();
        assertTrue(out.contains(" ignored due to TokenStreamException: "));
    }


}
