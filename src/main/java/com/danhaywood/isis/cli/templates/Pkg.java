package com.danhaywood.isis.cli.templates;

import java.io.IOException;

public class Pkg extends CliCommandAbstract {

    public String execute(final ExecutionContext ec) throws IOException {

        ec.getShellContext().clear();

        final Cd cd = new Cd();
        cd.setPackageName(getPackageName());
        cd.execute(ec);

        return ec.getShellContext().pwd(ec.getBaseDirCanonicalPath());
    }

}
