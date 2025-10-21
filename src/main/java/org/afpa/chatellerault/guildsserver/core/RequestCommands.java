package org.afpa.chatellerault.guildsserver.core;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class RequestCommands {
    private static final Map<String, Supplier<RequestCommand>> commandRegister = new HashMap<>();

    public static void register(String registryKey, Supplier<RequestCommand> supplier) throws RuntimeException {
        if (commandRegister.containsKey(registryKey)) {
            throw new RuntimeException(
                    "A command already registered: '%s'".formatted(registryKey)
            );
        }
        commandRegister.put(registryKey, supplier);
    }

    public static RequestCommand get(String registryKey) throws NoSuchElementException {
        Supplier<RequestCommand> supplier = commandRegister.get(registryKey);
        if (supplier == null) {
            throw new NoSuchElementException(
                    "No such command registered: '%s'".formatted(registryKey)
            );
        }
        return supplier.get();
    }
}
