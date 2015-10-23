package com.danhaywood.isis.cli;

import java.io.IOException;

import com.budhash.cliche.ShellFactory;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class IsisCliUsingCliche {

    public static void main(String[] args) throws IOException {

        final Options options = new Options();
        CmdLineParser parser = new CmdLineParser(options);
        try {
            parser.parseArgument(args);

            options.validate();
        } catch (CmdLineException e) {
            System.err.println(Banner.banner());
            parser.printUsage(System.err);
            System.err.println();
            System.err.println(e.getLocalizedMessage());
            System.err.println();
            System.err.flush();
            try {
                Thread.sleep(250); // cosmetics only, so stdout and stderr don't intermix
            } catch (InterruptedException e1) {
            }

            System.exit(1);
            return;
        }

        ShellFactory.createConsoleShell(
                "isis",
                Banner.banner()
                    + "\ntype ?list for available commands\n",
                new CliCommands (options.base, options.pkg))
                .commandLoop();
    }


}
