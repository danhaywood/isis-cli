package com.danhaywood.isis.cli;

import java.io.IOException;

import com.budhash.cliche.ShellFactory;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class IsisCli {

    public static void main(String[] args) throws IOException {

        final Options options = new Options();
        CmdLineParser parser = new CmdLineParser(options);
        try {
            parser.parseArgument(args);

            options.validate();
        } catch (CmdLineException e) {
            System.err.println(banner());
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
                banner()
                    + "\ntype ?list for available commands\n",
                new CliCommands (options.base, options.pkg))
                .commandLoop();
    }

    // http://patorjk.com/software/taag/#p=display&f=Ivrit&t=Apache%20Isis%20CLI
    private static String banner() {
        return "     _                     _            ___     _        ____ _     ___ \n"
                + "    / \\   _ __   __ _  ___| |__   ___  |_ _|___(_)___   / ___| |   |_ _|\n"
                + "   / _ \\ | '_ \\ / _` |/ __| '_ \\ / _ \\  | |/ __| / __| | |   | |    | | \n"
                + "  / ___ \\| |_) | (_| | (__| | | |  __/  | |\\__ \\ \\__ \\ | |___| |___ | | \n"
                + " /_/   \\_\\ .__/ \\__,_|\\___|_| |_|\\___| |___|___/_|___/  \\____|_____|___|\n"
                + "         |_|                                                            \n";
    }

}
