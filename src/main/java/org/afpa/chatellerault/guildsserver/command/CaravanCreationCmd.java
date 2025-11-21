package org.afpa.chatellerault.guildsserver.command;

import com.fasterxml.jackson.databind.JsonNode;
import org.afpa.chatellerault.guildsserver.core.RemoteCommand;
import org.afpa.chatellerault.guildsserver.model.Caravan;
import org.afpa.chatellerault.guildsserver.model.CaravanData;
import org.afpa.chatellerault.guildsserver.model.MapTile;
import org.afpa.chatellerault.guildsserver.model.TradingPost;
import org.afpa.chatellerault.guildsserver.service.Caravans;
import org.afpa.chatellerault.guildsserver.service.TradingPosts;

import java.sql.SQLException;
import java.util.UUID;

public class CaravanCreationCmd extends RemoteCommand<Caravan> {
    private String name;
    private UUID tradingPostId;

    @Override
    public void loadArguments(JsonNode params) {
        assert params != null;
        this.name = params.get("name").asText();
        this.tradingPostId = UUID.fromString(params.get("trading_post").asText());
    }

    @Override
    public Caravan execute() {
        TradingPost tradingPost = TradingPosts.findById(this.tradingPostId).orElseThrow();
        MapTile mapTile = tradingPost.getLocation().orElseThrow();
        Caravan newCaravan;
        try {
            newCaravan = Caravans.create(CaravanData.builder()
                    .name(this.name)
                    .mapTileId(mapTile.getId())
                    .build()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return newCaravan;
    }
}
