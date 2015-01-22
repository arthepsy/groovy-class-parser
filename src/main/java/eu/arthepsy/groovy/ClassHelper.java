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

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ClassHelper {
    private static Set<String> removableClassPathTails = getRemovableClassPathTails();

    private static String expandPath(String filePath) {
        File file = new File(filePath.replaceFirst("^~", System.getProperty("user.home")));
        return (file.isAbsolute() ? file.getAbsolutePath() : file.getPath());
    }
    public static String getClassPathFromFilePath(String filePath) {
        return getClassPathFromFilePath(filePath, null);
    }

    public static String getClassPathFromFilePath(String filePath, String baseDirectoryPath) {
        filePath = expandPath(filePath);
        String classPath;
        if (baseDirectoryPath != null && ! baseDirectoryPath.isEmpty()) {
            baseDirectoryPath = expandPath(baseDirectoryPath);
            classPath = new File(baseDirectoryPath).toPath().relativize(new File(filePath).toPath()).toString();
        } else {
            classPath = filePath;
        }
        int pos = classPath.lastIndexOf(".class");
        if (pos > -1) {
            classPath = classPath.substring(0, pos);
        }
        classPath = classPath.replace('/', '.');
        classPath = classPath.replaceAll("^\\.+", "");
        return classPath;
    }

    public static String getClassPathForClass(String classPath) {
        int pos = classPath.indexOf("$_");
        if (pos > -1) {
            classPath = classPath.substring(0, pos);
        }
        classPath = classPath.replaceFirst("\\$[0-9]+$", "");
        for (String removableTail: removableClassPathTails) {
            if (classPath.endsWith(removableTail)) {
                classPath = classPath.substring(0, classPath.length() - removableTail.length());
            }
        }
        return classPath;
    }

    private static Set<String> getRemovableClassPathTails() {
        Set<String> tails = new HashSet<String>();
        tails.add("$Trait$Helper");
        tails.add("$Trait$FieldHelper");
        return tails;
    }

}
