package com.danhaywood.isis.cli;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import com.budhash.cliche.Shell;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

class ShellContext {

    private final Stack<String> packageNames = new Stack<String>();
    private String className;
    private Shell shell;

    void setClassName(final String className) {
        this.className = className;
        sync();
    }

    public String getClassName() {
        return className;
    }

    void pushd(final String name) {
        packageNames.push(name);
        sync();
    }

    void popd() {
        if (className != null) {
            className = null;
        } else {
            if (!packageNames.empty()) {
                packageNames.pop();
            }
        }
        sync();
    }

    void clear() {
        className = null;
        packageNames.clear();
    }

    String asPackageName(final String separator) {
        return Joiner.on(separator).join(packageNames);
    }

    List<String> asPackageList() {
        return Lists.newArrayList(Arrays.asList(packageNames.toArray(new String[] {})));
    }

    void sync() {
        if (this.shell == null) {
            return;
        }
        final List<String> pathParts = asPackageList();
        if (className != null) {
            pathParts.add(className);
        }
        shell.setPath(pathParts);
    }

    public void setShell(final Shell shell) {
        this.shell = shell;
        sync();
    }

    public String pwd(final String baseDir) {
        return String.format(
                "base   : %s\npackage: %s\nclass  : %s",
                baseDir, asPackageName("."), (className != null? className: ""));
    }
}
