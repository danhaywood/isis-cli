package com.danhaywood.isis.cli.command;

import java.io.IOException;

import com.danhaywood.isis.cli.ShellContext;
import com.google.common.base.Splitter;

import org.jboss.aesh.cl.CommandDefinition;
import org.jboss.aesh.console.command.CommandResult;
import org.jboss.aesh.console.command.invocation.CommandInvocation;

@CommandDefinition(
        name="cd",
        description = "change directory (package)"
)
public class CdCommand extends AbstractCommand {

    public CdCommand(final ShellContext shellContext) {
        super(shellContext);
    }

    @Override
    public CommandResult execute(final CommandInvocation commandInvocation)
            throws IOException, InterruptedException {

        // split on '/', '\' and '.' (latter so long as the path doesn't contain "..")
        final String pattern = !shellContext.getPackageName().contains("..") ? "[/\\\\\\.]" : "[/\\\\]";

        final Iterable<String> packageNames =
                Splitter.onPattern(pattern).split(shellContext.getPackageName());
        for (String dirPart : packageNames) {
            if("..".equals(dirPart)) {
                shellContext.popd();
            } else {
                shellContext.pushd(dirPart);
            }
        }

        return CommandResult.SUCCESS;
    }
}
