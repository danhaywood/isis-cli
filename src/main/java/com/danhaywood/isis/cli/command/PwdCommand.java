package com.danhaywood.isis.cli.command;

import java.io.IOException;

import com.danhaywood.isis.cli.ShellContext;

import org.jboss.aesh.cl.CommandDefinition;
import org.jboss.aesh.console.command.CommandResult;
import org.jboss.aesh.console.command.invocation.CommandInvocation;

@CommandDefinition(
        name="pwd",
        description = "Prints current package"
)
public class PwdCommand extends AbstractCommand {

    public PwdCommand(final ShellContext shellContext) {
        super(shellContext);
    }

    @Override
    public CommandResult execute(final CommandInvocation commandInvocation)
            throws IOException, InterruptedException {

        commandInvocation.println(shellContext.pwd());

        return CommandResult.SUCCESS;
    }

}
