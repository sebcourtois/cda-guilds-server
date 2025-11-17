package org.afpa.chatellerault.guildsserver.core;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.lang.Nullable;

public interface RequestCommand {
    void loadParams(@Nullable JsonNode params);
    String execute();
}
