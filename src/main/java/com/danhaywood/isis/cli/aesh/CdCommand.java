package com.danhaywood.isis.cli.aesh;

import java.io.IOException;

import com.danhaywood.isis.cli.ExecutionContext;
import com.danhaywood.isis.cli.command.CliCommandAbstract;
import com.google.common.base.Splitter;

public class CdCommand extends CliCommandAbstract {

    public String execute(final ExecutionContext ec) throws IOException {

        // split on '/', '\' and '.' (latter so long as the path doesn't contain "..")
        final String pattern = !getPackageName().contains("..") ? "[/\\\\\\.]" : "[/\\\\]";

        final Iterable<String> packageNames = Splitter.onPattern(pattern).split(getPackageName());
        for (String dirPart : packageNames) {
            if("..".equals(dirPart)) {
                ec.getShellContext().popd();
            } else {
                ec.getShellContext().pushd(dirPart);
            }
        }

        return "";
    }

}
