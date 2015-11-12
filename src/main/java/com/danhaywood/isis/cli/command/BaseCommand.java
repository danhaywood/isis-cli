package com.danhaywood.isis.cli.command;

import java.io.File;
import java.io.IOException;

import com.danhaywood.isis.cli.Base;
import com.danhaywood.isis.cli.ShellContext;

import org.jboss.aesh.cl.Arguments;
import org.jboss.aesh.cl.CommandDefinition;
import org.jboss.aesh.console.command.CommandResult;
import org.jboss.aesh.console.command.invocation.CommandInvocation;

@CommandDefinition(
        name="base",
        description = "Sets current base dir (same as --base cmd line)"
)
public class BaseCommand extends AbstractCommand {

    @Arguments(description = "base directory, eg /c/tmp/test")
    private File baseDir;

    public BaseCommand(final ShellContext shellContext) {
        super(shellContext);
    }

    @Override
    public CommandResult execute(final CommandInvocation commandInvocation)
            throws IOException, InterruptedException {

        final String errorMessageIfAny = Base.validate(baseDir.getCanonicalPath());
        if(errorMessageIfAny != null) {
            commandInvocation.getShell().err().println(errorMessageIfAny);
            return CommandResult.FAILURE;
        }

        shellContext.setBaseDir(baseDir);
        commandInvocation.println(baseDir.getCanonicalPath());

        return CommandResult.SUCCESS;
    }

}
