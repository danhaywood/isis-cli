package com.danhaywood.isis.cli;

import java.io.IOException;

public interface CliCommand {
    String execute(ExecutionContext ec) throws IOException;

}
