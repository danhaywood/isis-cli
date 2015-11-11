package com.danhaywood.isis.cli;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import com.budhash.cliche.Command;
import com.budhash.cliche.Shell;
import com.budhash.cliche.ShellDependent;
import com.danhaywood.isis.cli.aesh.CdCommand;
import com.danhaywood.isis.cli.aesh.EntityCommand;
import com.danhaywood.isis.cli.aesh.PkgCommand;
import com.danhaywood.isis.cli.aesh.PropertyCommand;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class CliCommands implements ShellDependent {

    private final Configuration freemarkerCfg;
    private File baseDir;

    private final ShellContext shellContext = new ShellContext();

    public CliCommands(final String baseDir, final String packagePath) throws IOException {
        this.baseDir = new File(baseDir).getCanonicalFile();

        pkg(packagePath);

        final Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setClassForTemplateLoading(CliCommands.class, "command");

        cfg.setDefaultEncoding("UTF-8");
        cfg.setLocale(Locale.US);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        this.freemarkerCfg = cfg;
    }


    @Command(
            abbrev = "base",
            description = "base /users/xxx/dev/myapp    # set the base directory to the root of an Isis project (same as -base cmd line)"
    )
    public String base(final String base) throws IOException {
        final String errorMessageIfAny = Base.validate(base);
        if(errorMessageIfAny != null) {
            return errorMessageIfAny;
        }
        baseDir = new File(base);
        return getBaseDirCanonicalPath();
    }


    @Command(
            abbrev = "pwd",
            description = "print current base directory and package"
    )
    public String pwd() {
        return shellContext.pwd(getBaseDirCanonicalPath());
    }

    @Command(
            abbrev = "cd",
            description = "Change current package (use '.' or '/' as package separator; use '..' to go up)"
    )
    public void cd(final String packagePath) throws IOException {
        final CdCommand cdCommand = new CdCommand();
        cdCommand.setPackageName(packagePath);
        execute(cdCommand);
    }

    @Command(
            abbrev = "entity",
            description = "entity Customer   # scaffold new entity and supporting test classes"
    )
    public String entity(final String entityName) throws IOException {

        final EntityCommand entityCommand = new EntityCommand();
        entityCommand.setClassName(entityName);
        return execute(entityCommand);
    }

    @Command(
            abbrev = "isp",
            description = "isp firstName String   # create new property in current entity"
    )
    public String property(
            final String propertyName,
            final String dataType) throws IOException {

        final PropertyCommand propertyCommand = new PropertyCommand();
        propertyCommand.setPropertyName(propertyName);
        propertyCommand.setDataType(dataType);

        return execute(propertyCommand);
    }

    private String execute(final CliCommand cliCommand) throws IOException {
        return cliCommand.execute(newExecContext());
    }

    private String getBaseDirCanonicalPath() {
        try {
            return baseDir.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void cliSetShell(final Shell shell) {
        shellContext.setShell(shell);
    }

}
