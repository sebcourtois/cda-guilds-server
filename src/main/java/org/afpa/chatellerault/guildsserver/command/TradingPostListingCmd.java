package org.afpa.chatellerault.guildsserver.command;

import com.fasterxml.jackson.databind.JsonNode;
import org.afpa.chatellerault.guildsserver.core.RemoteCommand;
import org.afpa.chatellerault.guildsserver.service.TradingPosts;

public class TradingPostListingCmd extends RemoteCommand {
    @Override
    public void loadParams(JsonNode params) {
    }

    @Override
    public JsonNode execute() {
        JsonNode json;
        try {
            json = getJacksonMapper().valueToTree(
                    TradingPosts.findAll().toList()
            );
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
        return json;
    }
}
