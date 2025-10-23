package org.afpa.chatellerault.guildsserver.command;

import com.fasterxml.jackson.databind.JsonNode;
import org.afpa.chatellerault.guildsserver.core.RequestCommand;

public class SimpleEchoCmd implements RequestCommand {
    private String message;

    public SimpleEchoCmd(String message) {
        this.message = message;
    }

    public SimpleEchoCmd() {
    }

    public void loadParams(JsonNode paramsNode) {
        this.message = paramsNode.get("message").asText();
    }

    public String execute() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return this.message;
    }
}