package org.afpa.chatellerault.guildsserver.command;

import com.fasterxml.jackson.databind.JsonNode;
import org.afpa.chatellerault.guildsserver.core.RemoteCommand;

public class SimpleEchoCmd extends RemoteCommand<String> {
    private String message;

    public SimpleEchoCmd(String message) {
        this.message = message;
    }

    public SimpleEchoCmd() {
    }

    public void loadArguments(JsonNode paramsNode) {
        assert paramsNode != null;
        this.message = paramsNode.get("message").asText();
    }

    public String execute() {
        return this.message;
    }
}