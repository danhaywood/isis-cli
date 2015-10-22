package com.danhaywood.isis.cli;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import com.google.common.collect.Lists;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

public class JavaSourceFormatter {

    public String format(final IDocument doc) {
        final String source = doc.get();
        return format(source, doc);
    }
    public String format(final String source) {
        IDocument doc = new Document(source);
        return format(source, doc);
    }

    private String format(final String source, final IDocument doc) {

        final Hashtable options = JavaCore.getOptions();
        final List optionKeys = Lists.newArrayList(options.keySet());
        Collections.sort(optionKeys);

        options.put("org.eclipse.jdt.core.formatter.tabulation.char", "space");

        //        for (Object optionKey : optionKeys) {
        //            System.out.println(optionKey + "=" + options.get(optionKey));
        //        }

        CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(options);
        TextEdit textEdit = codeFormatter.format(CodeFormatter.K_UNKNOWN, source, 0, source.length(), 0, null);
        try {
            textEdit.apply(doc);
        } catch (BadLocationException e) {
            throw new IllegalArgumentException(e);
        }

        return doc.get();
    }

}
