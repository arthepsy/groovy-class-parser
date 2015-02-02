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

import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;
import org.codehaus.groovy.antlr.treewalker.VisitorAdapter;

import java.util.*;

class GroovyClassVisitor extends VisitorAdapter {
    private final Set<String> classPaths;
    private String packagePathWithSlashes;
    private String packagePathWithDots;

    private final Map<String, String> classCache;
    private final Stack<GroovySourceAST> stack;

    public GroovyClassVisitor() {
        packagePathWithSlashes = null;
        packagePathWithDots = null;
        classPaths = new HashSet<String>();
        classCache = new HashMap<String, String>();
        stack = new Stack<GroovySourceAST>();
    }

    public String getPackagePath() {
        return getPackagePath(false);
    }

    public String getPackagePath(Boolean withSlashes) {
        if (withSlashes) {
            return packagePathWithSlashes;
        } else {
            return packagePathWithDots;
        }
    }

    public Set<String> getClassPaths() {
        return classPaths;
    }

    @Override
    public void push(GroovySourceAST t) {
        stack.push(t);
    }

    @Override
    public GroovySourceAST pop() {
        if (! stack.empty()) {
            return stack.pop();
        }
        return null;
    }

    @Override
    public void visitPackageDef(GroovySourceAST t, int visit) {
        if (visit == OPENING_VISIT) {
            packagePathWithSlashes = getPackagePath(t);
            packagePathWithDots = packagePathWithSlashes.replace('/', '.');
        }
        super.visitPackageDef(t, visit);
    }

    @Override
    public void visitInterfaceDef(GroovySourceAST t, int visit) {
        visitClassDef(t, visit);
    }

    @Override
    public void visitTraitDef(GroovySourceAST t, int visit) {
        visitClassDef(t, visit);
    }

    @Override
    public void visitEnumDef(GroovySourceAST t, int visit) {
        visitClassDef(t, visit);
    }

    @Override
    public void visitAnnotationDef(GroovySourceAST t, int visit) {
        visitClassDef(t, visit);
    }

    @Override
    public void visitClassDef(GroovySourceAST t, int visit) {
        if (visit == OPENING_VISIT) {
            String className = getIdentFor(t);
            String classPath = className;
            String parentClassPath = getParentClassPath();
            if (parentClassPath != null) {
                classPath = parentClassPath + "$" + className;
                this.classCache.put(className, classPath);
            }
            classPath = getPackagePrefix() + classPath;
            classPaths.add(classPath);
        }
        super.visitClassDef(t, visit);
    }

    private GroovySourceAST getParentNode() {
        if (stack.empty()) {
            return null;
        }
        GroovySourceAST parentNode = null;
        GroovySourceAST currentNode = stack.pop();
        if (! stack.empty()) {
            parentNode = stack.peek();
        }
        stack.push(currentNode);
        return parentNode;
    }

    private boolean isTopLevelConstruct(GroovySourceAST node) {
        if (node == null) return false;
        int type = node.getType();
        return type == GroovyTokenTypes.CLASS_DEF
                || type == GroovyTokenTypes.INTERFACE_DEF
                || type == GroovyTokenTypes.TRAIT_DEF
                || type == GroovyTokenTypes.ANNOTATION_DEF
                || type == GroovyTokenTypes.ENUM_DEF;
    }

    private String getParentClassName() {
        if (stack.isEmpty()) {
            return null;
        }
        GroovySourceAST node = getParentNode();
        if (isTopLevelConstruct(node)) {
            return getIdentFor(node);
        }
        GroovySourceAST saved = stack.pop();
        String result = getParentClassName();
        stack.push(saved);
        return result;
    }

    private String getParentClassPath() {
        String className = getParentClassName();
        if (className != null) {
            String classPath = classCache.get(className);
            if (classPath != null) {
                className = classPath;
            }
        }
        return className;
    }

    private String getPackagePrefix() {
        if (packagePathWithDots != null && !packagePathWithDots.isEmpty()) {
            return packagePathWithDots + ".";
        } else {
            return "";
        }
    }

    private String getIdentFor(GroovySourceAST t) {
        return t.childOfType(GroovyTokenTypes.IDENT).getText();
    }

    private String getPackagePath(GroovySourceAST t) {
        GroovySourceAST child = t.childOfType(GroovyTokenTypes.DOT);
        if (child == null) {
            child = t.childOfType(GroovyTokenTypes.IDENT);
        }
        return recursePackageAST(child);
    }

    private String recursePackageAST(GroovySourceAST t) {
        if (t != null) {
            if (t.getType() == GroovyTokenTypes.DOT) {
                GroovySourceAST first = (GroovySourceAST) t.getFirstChild();
                GroovySourceAST second = (GroovySourceAST) first.getNextSibling();
                return (recursePackageAST(first) + "/" + recursePackageAST(second));
            }
            if (t.getType() == GroovyTokenTypes.IDENT) {
                return t.getText();
            }
        }
        return "";
    }


}
