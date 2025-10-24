package org.afpa.chatellerault.guildsserver.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.afpa.chatellerault.guildsserver.core.RequestCommand;
import org.afpa.chatellerault.guildsserver.model.TradingPost;
import org.afpa.chatellerault.guildsserver.service.TradingPosts;

public class TradingPostListingCmd implements RequestCommand {
    private static final com.fasterxml.jackson.databind.ObjectMapper
            jsonMapper = new com.fasterxml.jackson.databind.ObjectMapper();

    @Override
    public void loadParams(JsonNode params) {
    }

    @Override
    public String execute() {
        String json;
        try {
            json = jsonMapper.writeValueAsString(
                    TradingPosts.all()
                            .map(TradingPost::getData)
                            .toList()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }
}
