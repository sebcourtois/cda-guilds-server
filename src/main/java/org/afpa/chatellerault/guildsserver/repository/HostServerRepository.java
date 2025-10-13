package org.afpa.chatellerault.guildsserver.repository;

import org.afpa.chatellerault.guildsserver.core.BaseRepository;
import org.afpa.chatellerault.guildsserver.model.HostServerData;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class HostServerRepository extends BaseRepository<HostServerData> {
    public HostServerRepository(JdbcClient jdbcClient) {
        super(jdbcClient, new HostServerData.HostServerTable());
    }

}
