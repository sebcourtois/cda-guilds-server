package org.afpa.chatellerault.guildsserver.service;

import lombok.Setter;
import org.afpa.chatellerault.guildsserver.model.MapTile;
import org.afpa.chatellerault.guildsserver.model.MapTileData;
import org.afpa.chatellerault.guildsserver.repository.MapTileRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class MapTiles {
    @Setter
    private static MapTileRepository repository;

    public static MapTile create(MapTileData data) throws SQLException {
        repository.create(data);
        return new MapTile(data, repository);
    }

    public static int delete(MapTile mapTile) {
        return repository.delete(mapTile.getData());
    }

    public static int getRowCount() {
        return repository.numberOfRowsInTable(MapTileData.builder().build().tableName());
    }
}