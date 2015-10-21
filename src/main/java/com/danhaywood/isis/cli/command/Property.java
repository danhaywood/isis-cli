package com.danhaywood.isis.cli.command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import com.danhaywood.isis.cli.ExecutionContext;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;

public class Property extends CliCommandAbstract {

    @Getter @Setter
    private String packageName;
    @Getter @Setter
    private String className;

    @Getter @Setter
    private String propertyName;
    @Getter @Setter
    private String dataType;

    public String getPropertyNameUpper() {
        if(propertyName == null) {
            return null;
        }
        switch (propertyName.length()) {
        case 0:
            return propertyName;
        case 1:
            return propertyName.toUpperCase();
        default:
            final char firstChar = propertyName.charAt(0);
            return "" + Character.toUpperCase(firstChar) + propertyName.substring(1);
        }
    }

    public String execute(final ExecutionContext ec) throws IOException {

        setClassName(ec.getClassName());
        setPackageName(ec.getPackageName());

        final File packageDir = packageDirFor(ec, MvnModule.DOM, SrcPath.SRC_MAIN_JAVA);

        final String fileName = getClassName() + ".java";
        final File entityFile = new File(packageDir, fileName);

        if(!entityFile.exists()) {
            return String.format("Entity '%s' does not exist - skipping", getClassName());
        }

        final List<String> mergedLines = Lists.newArrayList();
        final List<String> originalLines = Files.readAllLines(entityFile.toPath());

        final int lastLineIdx = originalLines.size() - 1;
        final String lastLine = originalLines.get(lastLineIdx);

        final String propertyFragment = merge(ec, "dom");

        mergedLines.addAll(originalLines.subList(0, lastLineIdx));
        mergedLines.add(propertyFragment);
        mergedLines.add(lastLine);

        Files.write(entityFile.toPath(), mergedLines, Charsets.UTF_8);

        return String.format("Property '%s' created in entity '%s'", propertyName, getClassName());
    }

}
