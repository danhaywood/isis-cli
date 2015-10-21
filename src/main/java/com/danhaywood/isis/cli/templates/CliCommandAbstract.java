package com.danhaywood.isis.cli.templates;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.List;

import com.danhaywood.isis.cli.CliCommand;
import com.google.common.base.Joiner;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Getter;
import lombok.Setter;

public abstract class CliCommandAbstract implements CliCommand {

    @Getter @Setter
    private String packageName;
    @Getter @Setter
    private String className;

    protected File packageDirFor(
            final File baseDir,
            final String mvnModule,
            final String srcPath,
            final List<String> packageNames) {
        Path path = baseDir.toPath();
        path = path.resolve(mvnModule
                + "/"
                + srcPath);
        for (String packageName : packageNames) {
            path = path.resolve(packageName);
        }
        return path.toFile();
    }

    enum MvnModule {
        APP,
        DOM,
        FIXTURE,
        INTEG_TEST,
        WEBAPP;

        String getName() {
            return name().toLowerCase();
        }
    }

    protected enum SrcPath {
        SRC_MAIN_JAVA,
        SRC_MAIN_RESOURCES,
        SRC_TEST_JAVA,
        SRC_TEST_RESOURCES;

        String getPath() {
            return name().toLowerCase().replaceAll("_", "/");
        }
    }

    protected File packageDirFor(
            final ExecutionContext ec,
            final MvnModule mvnModule,
            final SrcPath srcPath) {

        final File baseDir = ec.getBaseDir();

        Path path = baseDir.toPath();
        path = path.resolve(mvnModule.getName() + "/" + srcPath.getPath());
        for (String packageName : ec.getPackageNames()) {
            path = path.resolve(packageName);
        }
        return path.toFile();
    }

    protected String merge(final ExecutionContext ec) throws IOException {
        return merge(ec, getClass().getSimpleName() + ".ftl", this);
    }

    private String merge(
            final ExecutionContext ec,
            final String freeMarkerTemplateName,
            final Object dataModel) throws IOException {

        if(this.packageName == null) {
            this.packageName = Joiner.on(".").join(ec.getPackageNames());
        }

        StringWriter sw = null;
        try {
            final Template template = ec.getFreemarkerCfg().getTemplate(freeMarkerTemplateName);

            sw = new StringWriter();
            template.process(dataModel, sw);
            return sw.toString();

        } catch (TemplateException e) {
            throw new RuntimeException(e);
        } finally {
            closeSafely(sw);
        }
    }

    protected static void closeSafely(final Closeable closeable) {
        if(closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                //
            }
        }
    }

}
