package com.danhaywood.isis.cli;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
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

    private final JavaSourceFormatter formatter = new JavaSourceFormatter();

    private String source;

    public JavaSource(final String source) {
        final String formatted = formatter.format(source);
        this.source = formatted;
    }

    public String getSource() {
        return source;
    }

    /**
     *
     * @param sourceFragment
     * @param fieldLocator - if finds field already present, then insert is skipped.
     * @param methodLocator - if finds method already present, then insert is skipped.
     */
    public boolean insert(
            final String sourceFragment,
            final Predicate<FieldDeclaration> fieldLocator,
            final Predicate<MethodDeclaration> methodLocator) {

        final CompilationUnit compilationUnit = getCompilationUnit(this.source);
        final TypeDeclaration typeDecl = getTypeDeclaration(compilationUnit);

        compilationUnit.recordModifications();

        final AST ast = compilationUnit.getAST();
        final ASTRewrite astRewrite = ASTRewrite.create(ast);

        // don't do anything if we detect that a field or method representing the code to be inserted is already present
        final FieldDeclaration[] fields = typeDecl.getFields();
        final Optional<FieldDeclaration> fieldDecl = Iterables.tryFind(Arrays.asList(fields), elseFalse(fieldLocator));
        if (fieldDecl.isPresent()) {
            return false;
        }

        final MethodDeclaration[] methods = typeDecl.getMethods();
        final Optional<MethodDeclaration> methodDecl = Iterables.tryFind(Arrays.asList(methods), elseFalse(methodLocator));
        if (methodDecl.isPresent()) {
            return false;
        }

        final BodyDeclaration bodyDecl = determineBodyDeclarationToInsertAfter(fields, methods);

        final Statement placeHolder = (Statement) astRewrite.createStringPlaceholder(sourceFragment, ASTNode.EMPTY_STATEMENT);
        ListRewrite listRewrite = astRewrite.getListRewrite(typeDecl, typeDecl.getBodyDeclarationsProperty());
        if(bodyDecl != null) {
            listRewrite.insertAfter(placeHolder, bodyDecl, null);
        } else {
            listRewrite.insertFirst(placeHolder, null);
        }

        final Document document = new Document(source);
        final TextEdit edits = astRewrite.rewriteAST(document, null);

        try {
            edits.apply(document);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }

        this.source = formatter.format(document);

        return true;
    }

    private static <T> Predicate<T> elseFalse(final Predicate<T> predicate) {
        return predicate != null ? predicate : Predicates.<T>alwaysFalse();
    }

    private static BodyDeclaration determineBodyDeclarationToInsertAfter(
                            final FieldDeclaration[] fields,
                            final MethodDeclaration[] methods) {
        if(methods.length > 0) {
            return methods[methods.length-1];
        }
        if(fields.length > 0) {
            return fields[fields.length-1];
        }
        return null;
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
