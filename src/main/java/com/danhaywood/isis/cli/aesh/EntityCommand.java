package com.danhaywood.isis.cli.aesh;

import java.io.File;
import java.io.IOException;

import com.danhaywood.isis.cli.ShellContext;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import org.jboss.aesh.console.command.CommandResult;
import org.jboss.aesh.console.command.invocation.CommandInvocation;

import lombok.Getter;
import lombok.Setter;

public class EntityCommand extends CommandAbstract {

    @Getter @Setter
    private String className;

    public EntityCommand(final ShellContext shellContext) {
        super(shellContext);
    }

    @Override
    public CommandResult execute(final CommandInvocation commandInvocation)
            throws IOException, InterruptedException {
        shellContext.setClassName(getClassName());

        setPackageName(shellContext.getPackageName());

        final File packageDir = packageDirFor(MvnModule.DOM, SrcPath.SRC_MAIN_JAVA);
        packageDir.mkdirs();

        final String fileName = getClassName() + ".java";
        final File entityFile = new File(packageDir, fileName);

        if(entityFile.exists()) {
            commandInvocation.println(String.format("Entity '%s' already exists", getClassName()));
            return CommandResult.FAILURE;
        }

        final String result = merge("dom");
        Files.write(result, entityFile, Charsets.UTF_8);

        commandInvocation.println(String.format("created %s in %s", fileName, packageDir.getCanonicalPath()));
        return CommandResult.SUCCESS;
    }
}
