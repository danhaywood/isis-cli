package com.danhaywood.isis.cli.command;

import java.io.File;
import java.io.IOException;

import com.danhaywood.isis.cli.JavaSource;
import com.danhaywood.isis.cli.ShellContext;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import org.jboss.aesh.cl.Arguments;
import org.jboss.aesh.cl.CommandDefinition;
import org.jboss.aesh.cl.Option;
import org.jboss.aesh.console.command.CommandResult;
import org.jboss.aesh.console.command.invocation.CommandInvocation;

import lombok.Getter;
import lombok.Setter;

@CommandDefinition(
        name = "isp",
        description = "isp firstName String   # create new property in current entity"
)
public class PropertyCommand extends AbstractCommand {

    @Getter @Setter @Option(defaultValue = "string")
    private String dataType;

    @Getter @Setter @Arguments
    private String propertyName;

    public PropertyCommand(final ShellContext shellContext) {
        super(shellContext);
    }

    @Override
    public CommandResult execute(final CommandInvocation commandInvocation)
            throws IOException, InterruptedException {

        final File packageDir = packageDirFor(MvnModule.DOM, SrcPath.SRC_MAIN_JAVA);

        final String fileName = shellContext.getClassName() + ".java";
        final File entityFile = new File(packageDir, fileName);

        if(!entityFile.exists()) {
            final String msg = String
                    .format("Entity '%s' does not exist - skipping", shellContext.getClassName());
            commandInvocation.getShell().err().println(msg);
            return CommandResult.FAILURE;
        }

        final String source = Files.toString(entityFile, Charsets.UTF_8);

        final String propertyFragment = merge("dom");

        final JavaSource javaSource = new JavaSource(source);
        final String propertyName = getPropertyName();
        final boolean inserted = javaSource.insert(propertyFragment, fieldLocatorFor(propertyName), null);
        if(!inserted) {
            final String msg = String
                    .format("Property '%s' already exists in entity '%s'", propertyName, shellContext.getClassName());
            commandInvocation.getShell().err().println(msg);
            return CommandResult.FAILURE;
        }

        Files.write(javaSource.getSource(), entityFile, Charsets.UTF_8);

        final String msg = String.format("Property '%s' created in entity '%s'", propertyName, shellContext.getClassName());
        commandInvocation.println(msg);

        return CommandResult.SUCCESS;
    }

    public String getPropertyNameUpper() {
        if(propertyName == null) {
            return null;
        }
        switch (propertyName.length()) {
        case 0:
            return propertyName;
        case 1:
            return propertyName.toUpperCase();
        default:
            final char firstChar = propertyName.charAt(0);
            return "" + Character.toUpperCase(firstChar) + propertyName.substring(1);
        }
    }

}
