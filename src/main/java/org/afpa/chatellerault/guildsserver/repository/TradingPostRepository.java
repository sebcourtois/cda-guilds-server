package org.afpa.chatellerault.guildsserver.repository;

import org.afpa.chatellerault.guildsserver.model.TradingPostData;
import org.afpa.chatellerault.guildsserver.util.BaseRepository;
import org.afpa.chatellerault.guildsserver.util.TableMappedObj;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class TradingPostRepository extends BaseRepository {

    public TradingPostRepository(JdbcClient jdbcClient) {
        super(jdbcClient);
    }

    public Optional<TableMappedObj> findByName(String someName) {
        String statement = "SELECT * FROM trading_post WHERE name = ?";

        return this.jdbcClient.sql(statement)
                .param(someName)
                .query(this.rowMapper(TradingPostData.builder().build()))
                .optional();
    }

    public Optional<TableMappedObj> findById(UUID someId) {
        String statement = "SELECT * FROM trading_post WHERE id = ?";

        var rowMapper = this.rowMapper(TradingPostData.builder().build());
        return this.jdbcClient.sql(statement)
                .param(someId)
                .query(rowMapper)
                .optional();
    }
}

