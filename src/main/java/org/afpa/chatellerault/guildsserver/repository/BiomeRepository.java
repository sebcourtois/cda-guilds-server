package org.afpa.chatellerault.guildsserver.repository;

import org.afpa.chatellerault.guildsserver.core.BaseRepository;
import org.afpa.chatellerault.guildsserver.model.BiomeData;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class BiomeRepository extends BaseRepository<BiomeData> {
    public BiomeRepository(JdbcClient jdbcClient) {
        super(jdbcClient, new BiomeData.BiomeTable());
    }
}