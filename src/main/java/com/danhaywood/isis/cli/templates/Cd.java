package com.danhaywood.isis.cli.templates;

import java.io.IOException;

import com.google.common.base.Splitter;

import lombok.Getter;
import lombok.Setter;

public class Cd extends CliCommandAbstract {

    @Getter @Setter
    private String packagePath;

    public String execute(final ExecutionContext ec) throws IOException {

        // split on '/', '\' and '.' (latter so long as the path doesn't contain "..")
        final String pattern = !packagePath.contains("..") ? "[/\\\\\\.]" : "[/\\\\]";

        final Iterable<String> packageNames = Splitter.onPattern(pattern).split(packagePath);
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
