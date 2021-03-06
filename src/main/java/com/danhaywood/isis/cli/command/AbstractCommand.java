package com.danhaywood.isis.cli.command;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.List;

import com.danhaywood.isis.cli.ShellContext;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.jboss.aesh.console.command.Command;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public abstract class AbstractCommand implements Command {

    protected final ShellContext shellContext;

    public AbstractCommand(final ShellContext shellContext) {
        this.shellContext = shellContext;
    }

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
            final MvnModule mvnModule,
            final SrcPath srcPath) {

        final File baseDir = shellContext.getBaseDir();

        Path path = baseDir.toPath();
        path = path.resolve(mvnModule.getName() + "/" + srcPath.getPath());
        for (final String packageName : shellContext.getPackageNames()) {
            path = path.resolve(packageName);
        }
        return path.toFile();
    }

    protected String merge(
            final String templateSuffix) throws IOException {
        final String templateName = String.format("%s.%s.ftl", getClass().getSimpleName(), templateSuffix);
        return merge(templateName, this);
    }

    private String merge(
            final String freeMarkerTemplateName,
            final Object dataModel) throws IOException {

        StringWriter sw = null;
        try {
            final Template template = shellContext.getFreemarkerCfg().getTemplate(freeMarkerTemplateName);

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

    protected static Predicate<FieldDeclaration> fieldLocatorFor(final String propertyName) {
        return fieldDeclaration -> {
            final List fragments = fieldDeclaration.fragments();
            for (Object fragment : fragments) {
                if (fragment instanceof VariableDeclarationFragment) {
                    final VariableDeclarationFragment vdf = (VariableDeclarationFragment) fragment;
                    if (Objects.equal(vdf.getName().getIdentifier(), propertyName)) {
                        return true;
                    }
                }
            }
            return false;
        };
    }




}