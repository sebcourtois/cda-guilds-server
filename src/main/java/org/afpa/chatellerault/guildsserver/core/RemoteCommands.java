package org.afpa.chatellerault.guildsserver.core;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class RemoteCommands {
    private static final HashMap<String, Supplier<RemoteCommand>> commandRegister = new HashMap<>();

    public static void register(String registryKey, Supplier<RemoteCommand> supplier) throws RuntimeException {
        if (commandRegister.containsKey(registryKey)) {
            throw new RuntimeException(
                    "A command already registered: '%s'".formatted(registryKey)
            );
        }
        commandRegister.put(registryKey, supplier);
    }

    public static RemoteCommand get(String registryKey) throws NoSuchElementException {
        Supplier<RemoteCommand> supplier = commandRegister.get(registryKey);
        if (supplier == null) {
            throw new NoSuchElementException(
                    "No such command registered: '%s'".formatted(registryKey)
            );
        }
        return supplier.get();
    }
}
