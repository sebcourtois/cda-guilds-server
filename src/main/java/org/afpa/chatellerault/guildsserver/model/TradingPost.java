package org.afpa.chatellerault.guildsserver.model;

import lombok.extern.log4j.Log4j2;
import org.afpa.chatellerault.guildsserver.core.BaseEntity;
import org.afpa.chatellerault.guildsserver.repository.TradingPostRepository;
import org.afpa.chatellerault.guildsserver.service.MapTiles;

import java.util.Optional;
import java.util.UUID;

@Log4j2
public class TradingPost extends BaseEntity<TradingPostData> {
    private MapTile location;

    public TradingPost(TradingPostData data, TradingPostRepository repository) {
        super(data, repository);
    }

    public UUID getId() {
        return this.data.getId();
    }

    public Optional<MapTile> getLocation() {
        if (this.location == null) {
            var mapTileId = this.data.getMapTileId();
            if (mapTileId != null) {
                log.info("Retrieving data of TradingPost's location");
                var found = MapTiles.findById(mapTileId);
                this.location = found.orElse(null);
            }
        }
        return Optional.ofNullable(this.location);
    }
}
