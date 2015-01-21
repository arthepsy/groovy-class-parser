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

import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.TokenStreamException;
import groovyjarjarantlr.collections.AST;
import org.codehaus.groovy.antlr.SourceBuffer;
import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;
import org.codehaus.groovy.antlr.treewalker.SourceCodeTraversal;
import org.codehaus.groovy.antlr.treewalker.Visitor;
import org.codehaus.groovy.tools.shell.util.Logger;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class ClassParser {
    private final Logger log = Logger.create(ClassParser.class);
    private final Set<String> classPaths;

    public ClassParser() {
        classPaths = new HashSet<String>();
    }

    public Set<String> getClassPaths() {
        return classPaths;
    }

    public void parseGroovyFile(String filePath) throws IOException {
        String fileName = new File(filePath).getName();
        try {
            String source = readFileAsString(filePath);
            ClassVisitor visitor = new ClassVisitor();
            this.parseGroovySource(source, visitor);

            for (String classPath : visitor.getClassPaths()) {
                classPaths.add(classPath);
            }
            String packagePath = visitor.getPackagePath();
            String classPathByFileName = fileName.replaceFirst("[.][^.]+$", "");
            if (packagePath != null && ! packagePath.isEmpty()) {
                classPathByFileName = packagePath + "." + classPathByFileName;
            }
            classPaths.add(classPathByFileName);
        } catch (OutOfMemoryError e) {
            log.error("Out of memory while processing: " + filePath);
            throw e;
        } catch (RecognitionException e) {
            String msg = "ignored due to RecognitionException: " + fileName + " [" + e.getMessage() + "]";
            log.error(msg);
            log.debug(msg, e);            e.printStackTrace();
        } catch (TokenStreamException e) {
            String msg = "ignored due to TokenStreamException: " + fileName + " [" + e.getMessage() + "]";
            log.error(msg);
            log.debug(msg, e);
        }
    }

    private void parseGroovySource(String source, Visitor visitor)
            throws RecognitionException, TokenStreamException {
        SourceBuffer sourceBuffer = new SourceBuffer();
        GroovyRecognizer parser = getGroovyParser(source, sourceBuffer);
        parser.compilationUnit();
        AST ast = parser.getAST();
        SourceCodeTraversal traverser = new SourceCodeTraversal(visitor);
        traverser.process(ast);
    }

    private GroovyRecognizer getGroovyParser(String source, SourceBuffer sourceBuffer) {
        return getGroovyParser(new StringReader(source), sourceBuffer);
    }
    private GroovyRecognizer getGroovyParser(Reader reader, SourceBuffer sourceBuffer) {
        UnicodeEscapingReader unicodeReader = new UnicodeEscapingReader(reader, sourceBuffer);
        GroovyLexer lexer = new GroovyLexer(unicodeReader);
        unicodeReader.setLexer(lexer);
        GroovyRecognizer parser = GroovyRecognizer.make(lexer);
        parser.setSourceBuffer(sourceBuffer);
        return parser;
    }

    private static String readFileAsString(String filePath) throws IOException {
        DataInputStream dis = new DataInputStream(new FileInputStream(filePath));
        try {
            long length = new File(filePath).length();
            if (length > Integer.MAX_VALUE) {
                throw new IOException("File " + filePath + " too large (" + length+ " bytes).");
            }
            byte[] bytes = new byte[(int)length];
            dis.readFully(bytes);
            return new String(bytes, "UTF-8");
        } finally {
            dis.close();
        }
    }

}
