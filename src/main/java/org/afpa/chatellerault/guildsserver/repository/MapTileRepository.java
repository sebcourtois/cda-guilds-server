package org.afpa.chatellerault.guildsserver.repository;

import org.afpa.chatellerault.guildsserver.core.BaseRepository;
import org.afpa.chatellerault.guildsserver.model.MapTileData;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class MapTileRepository extends BaseRepository<MapTileData> {
    public MapTileRepository(JdbcClient jdbcClient) {
        super(jdbcClient, new MapTileData.MapTileTable());
    }

    public Optional<MapTileData> findById(UUID someId) {
        String statement = "SELECT * FROM map_tile WHERE id = ?";

        var rowMapper = this.rowMapper(MapTileData.builder().build());
        return this.jdbcClient.sql(statement)
                .param(someId)
                .query(rowMapper)
                .optional();
    }
}
