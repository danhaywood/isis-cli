package com.danhaywood.isis.cli.aesh;

import java.io.IOException;

import com.danhaywood.isis.cli.ExecutionContext;

import org.jboss.aesh.cl.Arguments;
import org.jboss.aesh.cl.CommandDefinition;
import org.jboss.aesh.cl.Option;
import org.jboss.aesh.console.command.CommandResult;
import org.jboss.aesh.console.command.invocation.CommandInvocation;

@CommandDefinition(
        name="pkg",
        description = "package com.mycompany   # set the current package (same as -package cmd line)"
)
public class PkgCommand extends CommandAbstract {

    @Arguments(description = "package name, eg com.mycompany.myapp")
    private String packageName;

    public String pkg(final String packageName) throws IOException {
        final PkgCommand pkgCommand = new PkgCommand();
        pkgCommand.setPackageName(packageName);
        return execute(pkgCommand);
    }

    @Override
    public CommandResult execute(final CommandInvocation commandInvocation)
            throws IOException, InterruptedException {

        return CommandResult.SUCCESS;
    }

    public String execute(final ExecutionContext ec) throws IOException {

        ec.getShellContext().clear();

        final CdCommand cdCommand = new CdCommand();
        cdCommand.setPackageName(getPackageName());
        cdCommand.execute(ec);

        return ec.getShellContext().pwd(ec.getBaseDirCanonicalPath());
    }

}
