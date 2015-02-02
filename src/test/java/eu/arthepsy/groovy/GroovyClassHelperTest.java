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

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class GroovyClassHelperTest {
	
	private final String testFilePath = "eu/arthepsy/groovy/ClassPath.class";
	private final String testClassPath = "eu.arthepsy.groovy.ClassPath";

	private String getClassPath(String filePath) {
		return GroovyClassHelper.getClassPathFromFilePath(filePath);
	}

	private String getClassPath(String filePath, String baseDir) {
		return GroovyClassHelper.getClassPathFromFilePath(filePath, baseDir);
	}

	@Test
	public void RelativeFilePathTest() {
		String filePath = testFilePath;
		assertEquals(testClassPath, getClassPath(filePath));
	}

	@Test
	public void AbsoluteFilePathTest() {
		String filePath = "/" + testFilePath;
		assertEquals(testClassPath, getClassPath(filePath));
	}

	@Test
	public void RelativeFilePathWithBaseDirTest() {
		String baseDir = "src/main/java";
		String filePath = baseDir + "/" + testFilePath;
		assertEquals(testClassPath, getClassPath(filePath, baseDir));
	}

	@Test
	public void AbsoluteFilePathWithBaseDirTest() {
		String baseDir = "/sources/project";
		String filePath = baseDir + "/" + testFilePath;
		assertEquals(testClassPath, getClassPath(filePath, baseDir));
	}

	@Test
	public void FilePathWithoutClassExtensionTest() {
		String filePath = "ClassPath";
		assertEquals(filePath, getClassPath(filePath));
	}

	@Test
	public void RelativeFilePathWithEmptyBaseDirTest() {
		String baseDir = "";
		String filePath = testFilePath;
		assertEquals(testClassPath, getClassPath(filePath, baseDir));
	}

	@Test
	public void AbsoluteFilePathWithEmptyBaseDirTest() {
		String baseDir = "";
		String filePath = "/" + testFilePath;
		assertEquals(testClassPath, getClassPath(filePath, baseDir));
	}

	@Test
	public void ClassPathWithMethodTest() {
		String classPath = "eu.arthepsy.groovy.ClassPath$_methodOne";
		assertEquals(testClassPath, GroovyClassHelper.getClassPathForClass(classPath));
	}

	@Test
	public void AnonymousInnerClassPathTest() {
		String classPath = "eu.arthepsy.groovy.ClassPath$1";
		assertEquals(testClassPath, GroovyClassHelper.getClassPathForClass(classPath));
	}

	@Test
	public void ClassPathWithTraitTest() {
		String classPath;
		classPath = "eu.arthepsy.groovy.ClassPath$Trait$Helper";
		assertEquals(testClassPath, GroovyClassHelper.getClassPathForClass(classPath));
		classPath = "eu.arthepsy.groovy.ClassPath$Trait$FieldHelper";
		assertEquals(testClassPath, GroovyClassHelper.getClassPathForClass(classPath));
	}

}
