package org.afpa.chatellerault.guildsserver.command;

import com.fasterxml.jackson.databind.JsonNode;
import org.afpa.chatellerault.guildsserver.core.RemoteCommand;

public class SimpleEchoCmd extends RemoteCommand {
    private String message;

    public SimpleEchoCmd(String message) {
        this.message = message;
    }

    public SimpleEchoCmd() {
    }

    public void loadParams(JsonNode paramsNode) {
        assert paramsNode != null;
        this.message = paramsNode.get("message").asText();
    }

    public JsonNode execute() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        JsonNode json;
        try {
            json = getJacksonMapper().valueToTree(this.message);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
        return json;
    }
}