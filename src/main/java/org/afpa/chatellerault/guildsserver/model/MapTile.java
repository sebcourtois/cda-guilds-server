package org.afpa.chatellerault.guildsserver.model;

import org.afpa.chatellerault.guildsserver.core.BaseEntity;
import org.afpa.chatellerault.guildsserver.repository.MapTileRepository;

import java.util.UUID;

public class MapTile extends BaseEntity<MapTileData> {
    public MapTile(MapTileData data, MapTileRepository repository) {
        super(data, repository);
    }

    public UUID getId() {
        return this.data.getId();
    }
}
