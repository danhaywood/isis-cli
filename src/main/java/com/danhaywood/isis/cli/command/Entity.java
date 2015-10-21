package com.danhaywood.isis.cli.command;

import java.io.File;
import java.io.IOException;

import com.danhaywood.isis.cli.ExecutionContext;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import lombok.Getter;
import lombok.Setter;

public class Entity extends CliCommandAbstract {

    @Getter @Setter
    private String className;


    public String execute(final ExecutionContext ec) throws IOException {

        ec.getShellContext().setClassName(getClassName());

        setPackageName(ec.getPackageName());

        final File packageDir = packageDirFor(ec, MvnModule.DOM, SrcPath.SRC_MAIN_JAVA);
        packageDir.mkdirs();

        final String fileName = getClassName() + ".java";
        final File entityFile = new File(packageDir, fileName);

        if(entityFile.exists()) {
            return String.format("Entity '%s' already exists", getClassName());
        }

        final String result = merge(ec, "dom");
        Files.write(result, entityFile, Charsets.UTF_8);

        return String.format("created %s in %s", fileName, packageDir.getCanonicalPath());
    }


}