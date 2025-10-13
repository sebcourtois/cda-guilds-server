package org.afpa.chatellerault.guildsserver.repository;

import org.afpa.chatellerault.guildsserver.model.MapTileData;
import org.afpa.chatellerault.guildsserver.core.BaseRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class MapTileRepository extends BaseRepository<MapTileData> {
    public MapTileRepository(JdbcClient jdbcClient) {
        super(jdbcClient, new MapTileData.MapTileTable());
    }
}
