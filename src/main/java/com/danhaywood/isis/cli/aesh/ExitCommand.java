package com.danhaywood.isis.cli.aesh;

import java.io.IOException;

import com.danhaywood.isis.cli.ShellContext;

import org.jboss.aesh.cl.CommandDefinition;
import org.jboss.aesh.console.command.CommandResult;
import org.jboss.aesh.console.command.invocation.CommandInvocation;

@CommandDefinition(name="exit", description = "exit the program")
public class ExitCommand extends CommandAbstract {

    public ExitCommand(final ShellContext shellContext) {
        super(shellContext);
    }

    @Override
    public CommandResult execute(CommandInvocation invocation) throws IOException, InterruptedException {
        invocation.stop();
        return CommandResult.SUCCESS;
    }
}