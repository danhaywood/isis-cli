package com.danhaywood.isis.cli.command;

import java.io.IOException;

import com.danhaywood.isis.cli.ExecutionContext;

public class Pkg extends CliCommandAbstract {

    public String execute(final ExecutionContext ec) throws IOException {

        ec.getShellContext().clear();

        final Cd cd = new Cd();
        cd.setPackageName(getPackageName());
        cd.execute(ec);

        return ec.getShellContext().pwd(ec.getBaseDirCanonicalPath());
    }

}
