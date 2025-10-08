package org.afpa.chatellerault.guildsserver.model;

import org.afpa.chatellerault.guildsserver.repository.MapTileRepository;
import org.afpa.chatellerault.guildsserver.util.PersistedEntity;

import java.util.UUID;

public class MapTile extends PersistedEntity<MapTileData, MapTileRepository> {
    public MapTile(MapTileData data, MapTileRepository repository) {
        super(data, repository);
    }

    public UUID getId() {
        return this.data.getId();
    }
}
