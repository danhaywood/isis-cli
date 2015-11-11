package com.danhaywood.isis.cli;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import com.danhaywood.isis.cli.aesh.ExitCommand;

import org.jboss.aesh.console.AeshConsole;
import org.jboss.aesh.console.AeshConsoleBuilder;
import org.jboss.aesh.console.Prompt;
import org.jboss.aesh.console.command.registry.AeshCommandRegistryBuilder;
import org.jboss.aesh.console.command.registry.CommandRegistry;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.console.settings.SettingsBuilder;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class IsisCli {

    public static void main(String[] args) {

        System.err.println(Banner.banner());

        final Options options = new Options();
        CmdLineParser parser = new CmdLineParser(options);

        try {
            parser.parseArgument(args);

            options.validate();

            final String baseDirStr = options.getBase();
            File baseDir = new File(baseDirStr).getCanonicalFile();
            new IsisCli().run(baseDir, options.getPkg());

        } catch (CmdLineException | IOException e) {

            parser.printUsage(System.err);
            System.err.println();
            System.err.println(e.getLocalizedMessage());
            System.err.println();
            System.err.flush();

            sleepFor(250); // cosmetics only, so stdout and stderr don't intermix

            System.exit(1);
            return;
        }
    }

    static void sleepFor(final int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e1) {
        }
    }

    public void run(final File baseDir, final String pkg) {

        final Settings settings = createSettings();

        final Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setClassForTemplateLoading(CliCommands.class, "command");

        cfg.setDefaultEncoding("UTF-8");
        cfg.setLocale(Locale.US);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        final ShellContext shellContext = new ShellContext(cfg, baseDir, pkg);
        final CommandRegistry registry = createCommandRegistry(shellContext);
        final Prompt prompt = createPrompt();

        AeshConsole aeshConsole = new AeshConsoleBuilder()
            .commandRegistry(registry)
            .manProvider(createManProvider())
            .settings(settings)
            .prompt(prompt)
            .create();

        aeshConsole.start();
    }

    protected ManProviderForIsisCli createManProvider() {
        return new ManProviderForIsisCli();
    }

    protected Settings createSettings() {
        return new SettingsBuilder()
            .logging(true)
            .enableMan(true)
            .readInputrc(false)
            .create();
    }

    protected Prompt createPrompt() {
        return new Prompt("isis $ ");
    }

    protected CommandRegistry createCommandRegistry(final ShellContext shellContext) {
        return new AeshCommandRegistryBuilder()
                .command(new ExitCommand(shellContext))
    //            .command(AeshExample.LsCommand.class)
    //            .command(TestConsoleCommand.class)
                .create();
    }

}