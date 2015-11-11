package com.danhaywood.isis.cli;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public final class Base {

    public static String validate(final String base) throws IOException {
        final File baseDir = new File(base).getCanonicalFile();
        if(!baseDir.isDirectory()) {
            return "Base is not a directory";
        }

        final List<File> files = Arrays.asList(baseDir.listFiles());

        final List<String> fileNames = Arrays.asList("pom.xml");
        for (String fileName : fileNames) {
            final Optional<File> optFile = Iterables.tryFind(files, isFile(fileName));
            if(!optFile.isPresent()) {
                return "Could not locate '" + fileName + "' in base dir '" + baseDir + "'";
            }
        }

        final List<String> directories = Arrays.asList("dom", "fixture", "integtest", "webapp");
        for (String directory : directories) {
            final Optional<File> optDir = Iterables.tryFind(files, isDir(directory));
            if(!optDir.isPresent()) {
                return "Could not locate '" + directory + "' in base dir '" + baseDir + "'";
            }
        }
        return null;
    }

    private static Predicate<File> isFile(final String fileName) {
        return file -> file.getName().equals(fileName) && file.isFile();
    }

    private static Predicate<File> isDir(final String dirName) {
        return file -> file.getName().contains(dirName) && file.isDirectory();
    }

}
