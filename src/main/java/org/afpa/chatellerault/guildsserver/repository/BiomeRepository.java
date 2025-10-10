package org.afpa.chatellerault.guildsserver.repository;

import org.afpa.chatellerault.guildsserver.model.BiomeData;
import org.afpa.chatellerault.guildsserver.util.BaseRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class BiomeRepository extends BaseRepository<BiomeData> {

    public BiomeRepository(JdbcClient jdbcClient) {
        super(jdbcClient);
    }

}