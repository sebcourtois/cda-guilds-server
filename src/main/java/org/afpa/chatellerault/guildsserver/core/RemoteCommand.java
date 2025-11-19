package org.afpa.chatellerault.guildsserver.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

public abstract class RemoteCommand {
    @Getter
    @Setter
    private static ObjectMapper jacksonMapper = null;

    public abstract void loadParams(@Nullable JsonNode params);

    public abstract JsonNode execute();
}
