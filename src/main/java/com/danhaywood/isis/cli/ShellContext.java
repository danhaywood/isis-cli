package com.danhaywood.isis.cli;

import java.io.File;
import java.util.Arrays;
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
    @Getter
    private final File baseDir;
    @Getter @Setter
    private String className;

    public ShellContext(
            final Configuration freemarkerCfg,
            final File baseDir,
            final String pkg) {
        this.freemarkerCfg = freemarkerCfg;
        this.baseDir = baseDir;
        pushd(pkg);
    }


    public String getPackageName() {
        return packageNames.empty()? null : packageNames.peek();
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

    String asPackageName(final String separator) {
        return Joiner.on(separator).join(packageNames);
    }

    public List<String> asPackageList() {
        return Lists.newArrayList(Arrays.asList(packageNames.toArray(new String[] {})));
    }

    public String pwd(final String baseDir) {
        return String.format(
                "base   : %s\npackage: %s\nclass  : %s",
                baseDir, asPackageName("."), (className != null? className: ""));
    }


}
