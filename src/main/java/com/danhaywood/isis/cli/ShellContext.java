package com.danhaywood.isis.cli;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import freemarker.template.Configuration;
import lombok.Getter;
import lombok.Setter;

public class ShellContext {

    private final Stack<String> packageNames = new Stack<String>();

    @Getter
    private final Configuration freemarkerCfg;
    @Getter @Setter
    private File baseDir;
    @Getter @Setter
    private String className;

    public ShellContext(
            final Configuration freemarkerCfg,
            final File baseDir,
            final String fullyQualifiedPackage) {
        this.freemarkerCfg = freemarkerCfg;
        this.baseDir = baseDir;
        pushd(fullyQualifiedPackage);
    }

    public String getPackageName() {
        return asPackageName(".");
    }

    private String asPackageName(final String separator) {
        return Joiner.on(separator).join(packageNames);
    }

    private List<String> asPackageList() {
        return Lists.newArrayList(Arrays.asList(packageNames.toArray(new String[] {})));
    }
    public List<String> getPackageNames() {
        return Collections.unmodifiableList(asPackageList());
    }

    public void pushd(final String name) {
        packageNames.push(name);
    }

    public void popd() {
        if (className != null) {
            className = null;
        } else {
            if (!packageNames.empty()) {
                packageNames.pop();
            }
        }
    }

    public void clear() {
        className = null;
        packageNames.clear();
    }

    public String pwd() {
        return getPackageName() + (className != null? "." + className : "");
    }
}
