package com.danhaywood.isis.cli;

import org.jboss.aesh.console.AeshConsole;
import org.jboss.aesh.console.AeshConsoleBuilder;
import org.jboss.aesh.console.Prompt;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.console.settings.SettingsBuilder;

public class IsisCli {
    public static void main(String[] args) {
        Settings settings = new SettingsBuilder()
            .logging(true)
            .enableMan(true)
            .readInputrc(false)
            .create();
//        CommandRegistry registry = new AeshCommandRegistryBuilder()
//            .command(ExitCommand.class)
//            .command(AeshExample.LsCommand.class)
//            .command(TestConsoleCommand.class)
//            .create();
        AeshConsole aeshConsole = new AeshConsoleBuilder()
//            .commandRegistry(registry)
//            .manProvider(new ManProviderExample())
            .settings(settings)
            .prompt(new Prompt("[aesh@rules]$ "))
            .create();

        aeshConsole.start();
    }
}