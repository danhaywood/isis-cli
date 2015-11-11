package com.danhaywood.isis.cli;

import java.io.IOException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

public class Options {

    @Option(
            name = "-base",
            aliases = {"-b"},
            usage = "Base directory (must exist and be root of Isis application)",
            required = false
    )
    private String base;

    public String getBase() {
        return base != null? base: ".";
    }

    @Option(
            name = "-package",
            aliases = {"-p"},
            usage = "Package",
            required = false
    )
    private String pkg;

    public String getPkg() {
        return pkg != null? pkg: "domainapp.dom";
    }

    void validate() throws CmdLineException {
        try {
            final String errorMessageIfAny = Base.validate(getBase());
            if(errorMessageIfAny != null) {
                throw new CmdLineException(errorMessageIfAny);
            }
        } catch (IOException e) {
            throw new CmdLineException(e);
        }

    }

}
