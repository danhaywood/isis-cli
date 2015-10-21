package com.danhaywood.isis.cli;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import freemarker.template.Configuration;

public interface CliCommand {
    String execute(ExecutionContext ec) throws IOException;

    class ExecutionContext {

        private final Configuration cfg;
        private final File baseDir;
        private final ShellContext shellContext;

        public ExecutionContext(
                final Configuration cfg,
                final File baseDir,
                final ShellContext shellContext) {
            this.cfg = cfg;
            this.baseDir = baseDir;
            this.shellContext = shellContext;
        }

        public Configuration getFreemarkerCfg() {
            return cfg;
        }

        public File getBaseDir() {
            return baseDir;
        }

        public String getClassName() {
            return shellContext.getClassName();
        }

        public List<String> getPackageNames() {
            return Collections.unmodifiableList(shellContext.asPackageList());
        }

        public String getPackageName() {
            return shellContext.asPackageName(".");
        }
    }
}