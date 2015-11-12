package com.danhaywood.isis.cli.command;

import java.io.IOException;

import com.danhaywood.isis.cli.ShellContext;

import org.jboss.aesh.cl.Arguments;
import org.jboss.aesh.cl.CommandDefinition;
import org.jboss.aesh.console.command.CommandResult;
import org.jboss.aesh.console.command.invocation.CommandInvocation;

@CommandDefinition(
        name="package",
        description = "package com.mycompany   # set the current package (same as --package cmd line)"
)
public class PackageCommand extends AbstractCommand {

    @Arguments(description = "package name, eg com.mycompany.myapp")
    private String packageName;

    public PackageCommand(final ShellContext shellContext) {
        super(shellContext);
    }

    @Override
    public CommandResult execute(final CommandInvocation commandInvocation)
            throws IOException, InterruptedException {

        shellContext.clear();

        final CdCommand cdCommand = new CdCommand(shellContext);
        cdCommand.execute(commandInvocation);

        return CommandResult.SUCCESS;
    }

}
