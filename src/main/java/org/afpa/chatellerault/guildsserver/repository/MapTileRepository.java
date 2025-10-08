package org.afpa.chatellerault.guildsserver.repository;

import org.afpa.chatellerault.guildsserver.util.BaseRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class MapTileRepository extends BaseRepository {
    public MapTileRepository(JdbcClient jdbcClient) {
        super(jdbcClient);
    }
}
