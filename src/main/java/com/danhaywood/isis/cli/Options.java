package com.danhaywood.isis.cli;

import java.io.IOException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

import lombok.Getter;

public class Options {

    @Getter
    @Option(
            name = "-base",
            aliases = {"-b"},
            usage = "Base directory (must exist and be root of Isis application)",
            required = true
    )
    String base;

    @Option(
            name = "-package",
            aliases = {"-p"},
            usage = "Package",
            required = true
    )
    String pkg;


    void validate() throws CmdLineException {
        try {
            final String errorMessageIfAny = Base.validate(this.base);
            if(errorMessageIfAny != null) {
                throw new CmdLineException(errorMessageIfAny);
            }
        } catch (IOException e) {
            throw new CmdLineException(e);
        }

    }

}
