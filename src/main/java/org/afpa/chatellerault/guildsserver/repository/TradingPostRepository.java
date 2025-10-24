package org.afpa.chatellerault.guildsserver.repository;

import org.afpa.chatellerault.guildsserver.core.BaseRepository;
import org.afpa.chatellerault.guildsserver.core.TableConfigRowMapper;
import org.afpa.chatellerault.guildsserver.model.TradingPostData;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public class TradingPostRepository extends BaseRepository<TradingPostData> {

    public TradingPostRepository(JdbcClient jdbcClient) {
        super(
                jdbcClient,
                new TradingPostData.TradingPostTable(),
                TradingPostData.builder()::build
        );
    }

    public Optional<TradingPostData> findByName(String someName) {
        String statement = "SELECT * FROM trading_post WHERE name = ?";

        return this.jdbcClient.sql(statement)
                .param(someName)
                .query(this.rowMapper())
                .optional();
    }

    public Optional<TradingPostData> findById(UUID someId) {
        String statement = "SELECT * FROM trading_post WHERE id = ?";

        TableConfigRowMapper<TradingPostData> rowMapper = this.rowMapper();
        return this.jdbcClient.sql(statement)
                .param(someId)
                .query(rowMapper)
                .optional();
    }
}

