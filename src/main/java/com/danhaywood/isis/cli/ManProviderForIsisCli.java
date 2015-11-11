package com.danhaywood.isis.cli;

import java.io.InputStream;

import org.jboss.aesh.console.helper.ManProvider;

public class ManProviderForIsisCli implements ManProvider {
    @Override
    public InputStream getManualDocument(final String s) {
        return null;
    }
}
