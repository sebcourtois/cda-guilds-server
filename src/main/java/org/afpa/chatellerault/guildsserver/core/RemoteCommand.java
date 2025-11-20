package org.afpa.chatellerault.guildsserver.core;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.lang.Nullable;

public abstract class RemoteCommand<R> {
    public abstract void loadArguments(@Nullable JsonNode params);
    public abstract R execute();
}
