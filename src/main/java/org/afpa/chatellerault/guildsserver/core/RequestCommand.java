package org.afpa.chatellerault.guildsserver.core;

import com.fasterxml.jackson.databind.JsonNode;

public interface RequestCommand {
    void loadParams(JsonNode params);
    String execute();
}
