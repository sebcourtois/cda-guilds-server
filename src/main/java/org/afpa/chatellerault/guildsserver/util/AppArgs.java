package org.afpa.chatellerault.guildsserver.util;

import org.springframework.boot.ApplicationArguments;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public final class AppArgs {
    private final ApplicationArguments sourceArgs;

    public AppArgs(ApplicationArguments sourceArgs) {
        this.sourceArgs = sourceArgs;
    }

    public Optional<Path> singlePathForOption(String optionName) {
        if (!sourceArgs.containsOption(optionName)) return Optional.empty();

        List<String> optionValues = sourceArgs.getOptionValues(optionName);
        if (optionValues.isEmpty()) {
            String msg = "No value passed to command-line option: '%s'".formatted(optionName);
            throw new RuntimeException(msg);
        }
        if (optionValues.size() > 1) {
            String msg = "More than one value passed to command-line option: '%s'".formatted(optionName);
            throw new RuntimeException(msg);
        }
        return Optional.of(Path.of(optionValues.getFirst()));
    }

    public String[] getSourceArgs() {
        return sourceArgs.getSourceArgs();
    }

    public boolean containsOption(String name) {
        return sourceArgs.containsOption(name);
    }
}
