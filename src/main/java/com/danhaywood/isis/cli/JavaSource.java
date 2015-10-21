package com.danhaywood.isis.cli;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

public class JavaSource {

    private String source;

    public JavaSource(final String source) {

        final CompilationUnit compilationUnit = getCompilationUnit(source);
        final TypeDeclaration typeDecl = getTypeDeclaration(compilationUnit);

        this.source = typeDecl.toString();
    }

    public void insert(
            final String toInsert,
            Predicate<BodyDeclaration> locator) throws BadLocationException {

        final CompilationUnit compilationUnit = getCompilationUnit(this.source);
        final TypeDeclaration typeDecl = getTypeDeclaration(compilationUnit);

        compilationUnit.recordModifications();

        final AST ast = compilationUnit.getAST();
        final ASTRewrite astRewrite = ASTRewrite.create(ast);

        // don't do anything if we detect that a field or method representing the code to be inserted is already present
        final FieldDeclaration[] fields = typeDecl.getFields();
        final Optional<BodyDeclaration> fieldDecl = Iterables.tryFind(Arrays.<BodyDeclaration>asList(fields), locator);
        if(fieldDecl.isPresent()) {
            return;
        }
        final MethodDeclaration[] methods = typeDecl.getMethods();
        final Optional<BodyDeclaration> methodDecl = Iterables.tryFind(Arrays.<BodyDeclaration>asList(methods), locator);

        if(methodDecl.isPresent()) {
            return;
        }

        BodyDeclaration bodyDecl;
        if(methods.length > 0) {
            bodyDecl = methods[methods.length-1];
        } else if(fields.length > 0) {
            bodyDecl = fields[fields.length-1];
        } else {
            bodyDecl = null;
        }

        Statement placeHolder = (Statement) astRewrite.createStringPlaceholder(toInsert, ASTNode.EMPTY_STATEMENT);

        ListRewrite listRewrite = astRewrite.getListRewrite(typeDecl, typeDecl.getBodyDeclarationsProperty());
        if(bodyDecl != null) {
            listRewrite.insertAfter(placeHolder, bodyDecl, null);
        } else {
            listRewrite.insertFirst(placeHolder, null);
        }

        final Document document = new Document(source);
        final TextEdit edits = astRewrite.rewriteAST(document, null);

        edits.apply(document);

        final String afterEdits = document.get();

        final CompilationUnit compilationUnitAfter = getCompilationUnit(afterEdits);
        final TypeDeclaration typeDecl2After = getTypeDeclaration(compilationUnitAfter);

        this.source = typeDecl2After.toString();
    }

    public String getSource() {
        return source;
    }

    private static TypeDeclaration getTypeDeclaration(final CompilationUnit compilationUnit) {
        final List<TypeDeclaration> types = compilationUnit.types();
        return types.get(0);
    }

    private static CompilationUnit getCompilationUnit(final String source) {
        final ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(source.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        return (CompilationUnit)parser.createAST(null);
    }

}
