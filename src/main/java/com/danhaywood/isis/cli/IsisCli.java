package com.danhaywood.isis.cli;

import java.io.IOException;
import java.util.List;

import com.budhash.cliche.ShellFactory;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class IsisCli {

    public static void main(final String[] args) throws JavaModelException, BadLocationException {

        final String source = "public class Customer { public void foo() {} private String firstName; private String lastName; }";

        final CompilationUnit compilationUnit = getCompilationUnit(source);
        final TypeDeclaration typeDecl = getTypeDeclaration(compilationUnit);

        System.out.println(typeDecl.toString());

        compilationUnit.recordModifications();

        final AST ast = compilationUnit.getAST();
        final ASTRewrite astRewrite = ASTRewrite.create(ast);

        final FieldDeclaration[] fields = typeDecl.getFields();
        FieldDeclaration fieldDecl = fields[1];

        Statement placeHolder = (Statement) astRewrite.createStringPlaceholder("public void bar(){}", ASTNode.EMPTY_STATEMENT);

        ListRewrite listRewrite = astRewrite.getListRewrite(typeDecl, typeDecl.getBodyDeclarationsProperty());
        listRewrite.insertAfter(placeHolder, fieldDecl, null);


        Document document = new Document(source);
        TextEdit edits = astRewrite.rewriteAST(document, null);

        edits.apply(document);

        final String s = document.get();

        System.out.println(s);

        final CompilationUnit compilationUnit2 = getCompilationUnit(s);
        final TypeDeclaration typeDecl2 = getTypeDeclaration(compilationUnit2);

        System.out.println(typeDecl2.toString());

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

    public static void main2(String[] args) throws IOException {

        final Options options = new Options();
        CmdLineParser parser = new CmdLineParser(options);
        try {
            parser.parseArgument(args);

            options.validate();
        } catch (CmdLineException e) {
            System.err.println(banner());
            parser.printUsage(System.err);
            System.err.println();
            System.err.println(e.getLocalizedMessage());
            System.err.println();
            System.err.flush();
            try {
                Thread.sleep(250); // cosmetics only, so stdout and stderr don't intermix
            } catch (InterruptedException e1) {
            }

            System.exit(1);
            return;
        }

        ShellFactory.createConsoleShell(
                "isis",
                banner()
                    + "\ntype ?list for available commands\n",
                new CliCommands (options.base, options.pkg))
                .commandLoop();
    }

    // http://patorjk.com/software/taag/#p=display&f=Ivrit&t=Apache%20Isis%20CLI
    private static String banner() {
        return "     _                     _            ___     _        ____ _     ___ \n"
                + "    / \\   _ __   __ _  ___| |__   ___  |_ _|___(_)___   / ___| |   |_ _|\n"
                + "   / _ \\ | '_ \\ / _` |/ __| '_ \\ / _ \\  | |/ __| / __| | |   | |    | | \n"
                + "  / ___ \\| |_) | (_| | (__| | | |  __/  | |\\__ \\ \\__ \\ | |___| |___ | | \n"
                + " /_/   \\_\\ .__/ \\__,_|\\___|_| |_|\\___| |___|___/_|___/  \\____|_____|___|\n"
                + "         |_|                                                            \n";
    }

}
