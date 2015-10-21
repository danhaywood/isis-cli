package com.danhaywood.isis.cli;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import com.budhash.cliche.Command;
import com.budhash.cliche.Shell;
import com.budhash.cliche.ShellDependent;
import com.danhaywood.isis.cli.templates.Entity;
import com.danhaywood.isis.cli.templates.Property;
import com.google.common.base.Splitter;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class CliCommands implements ShellDependent {

    private final Configuration freemarkerCfg;
    private File baseDir;

    private final ShellContext shellContext = new ShellContext();

    public CliCommands(final String baseDir, final String packagePath) {
        try {
            this.baseDir = new File(baseDir).getCanonicalFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        pkg(packagePath);

        final Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setClassForTemplateLoading(CliCommands.class, "templates");

        cfg.setDefaultEncoding("UTF-8");
        cfg.setLocale(Locale.US);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        this.freemarkerCfg = cfg;
    }

    private CliCommand.ExecutionContext newExecContext() {
        return new CliCommand.ExecutionContext(freemarkerCfg, baseDir, shellContext);
    }

    @Command(
            abbrev = "cd",
            description = "Change current package (use '.' or '/' as package separator; use '..' to go up)"
    )
    public void cd(final String packagePath) {

        // split on '/', '\' and '.' (latter so long as the path doesn't contain "..")
        final String pattern = !packagePath.contains("..") ? "[/\\\\\\.]" : "[/\\\\]";

        final Iterable<String> packageNames = Splitter.onPattern(pattern).split(packagePath);
        for (String dirPart : packageNames) {
            if("..".equals(dirPart)) {
                shellContext.popd();
            } else {
                shellContext.pushd(dirPart);
            }
        }
    }

    @Command(
            abbrev = "base",
            description = "Set the current base directory to the root of the Isis project (same as -base cmd line)"
    )
    public String base(final String base) throws IOException {
        final String errorMessageIfAny = Base.validate(base);
        if(errorMessageIfAny != null) {
            return errorMessageIfAny;
        }
        baseDir = new File(base);
        return baseDir();
    }

    @Command(
            abbrev = "package",
            description = "package com.mycompany   # set the current package (same as -package cmd line)"
    )
    public String pkg(final String packageName) {
        shellContext.clear();
        cd(packageName);
        return baseDir();
    }

    @Command(
            abbrev = "pwd",
            description = "print current base directory and package"
    )
    public String pwd() {
        return shellContext.pwd(baseDir());
    }

    @Command(
            abbrev = "entity",
            description = "entity Customer   # scaffold new entity and supporting test classes"
    )
    public String entity(final String entityName) throws IOException {

        final Entity entity = new Entity();
        shellContext.setClassName(entityName);

        return entity.execute(newExecContext());
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

        return property.execute(newExecContext());
    }


    private String baseDir() {
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
