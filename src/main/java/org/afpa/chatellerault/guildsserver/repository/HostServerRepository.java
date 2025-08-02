package org.afpa.chatellerault.guildsserver.repository;

import org.afpa.chatellerault.guildsserver.util.BaseRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class HostServerRepository extends BaseRepository {
    public HostServerRepository(JdbcClient jdbcClient) {
        super(jdbcClient);
    }
}
