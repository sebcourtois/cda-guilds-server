package org.afpa.chatellerault.guildsserver.command;

import com.fasterxml.jackson.databind.JsonNode;
import org.afpa.chatellerault.guildsserver.core.RemoteCommand;
import org.afpa.chatellerault.guildsserver.model.TradingPost;
import org.afpa.chatellerault.guildsserver.service.TradingPosts;

import java.util.List;

public class TradingPostListingCmd extends RemoteCommand<List<TradingPost>> {
    @Override
    public void loadArguments(JsonNode params) {
    }

    @Override
    public List<TradingPost> execute() {
        return TradingPosts.findAll().toList();
    }
}
