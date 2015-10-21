package com.danhaywood.isis.cli;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import com.budhash.cliche.Command;
import com.budhash.cliche.Shell;
import com.budhash.cliche.ShellDependent;
import com.danhaywood.isis.cli.command.Cd;
import com.danhaywood.isis.cli.command.Entity;
import com.danhaywood.isis.cli.command.Pkg;
import com.danhaywood.isis.cli.command.Property;

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

    private ExecutionContext newExecContext() {
        return new ExecutionContext(freemarkerCfg, baseDir, shellContext);
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
            abbrev = "package",
            description = "package com.mycompany   # set the current package (same as -package cmd line)"
    )
    public String pkg(final String packageName) throws IOException {
        final Pkg pkg = new Pkg();
        pkg.setPackageName(packageName);
        return execute(pkg);
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
        final Cd cd = new Cd();
        cd.setPackageName(packagePath);
        execute(cd);
    }

    @Command(
            abbrev = "entity",
            description = "entity Customer   # scaffold new entity and supporting test classes"
    )
    public String entity(final String entityName) throws IOException {

        final Entity entity = new Entity();
        entity.setClassName(entityName);
        return execute(entity);
    }

    @Command(
            abbrev = "prop",
            description = "prop firstName String   # create new property in current entity"
    )
    public String property(
            final String propertyName,
            final String dataType) throws IOException {

        final Property property = new Property();
        property.setPropertyName(propertyName);
        property.setDataType(dataType);

        return execute(property);
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
