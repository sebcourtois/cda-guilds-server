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
        super(jdbcClient, new TradingPostData.TradingPostTable());
    }

    public Optional<TradingPostData> findByName(String someName) {
        String statement = "SELECT * FROM trading_post WHERE name = ?";

        return this.jdbcClient.sql(statement)
                .param(someName)
                .query(this.rowMapper(TradingPostData.builder()::build))
                .optional();
    }

    public Optional<TradingPostData> findById(UUID someId) {
        String statement = "SELECT * FROM trading_post WHERE id = ?";

        TableConfigRowMapper<TradingPostData> rowMapper = this.rowMapper(TradingPostData.builder()::build);
        return this.jdbcClient.sql(statement)
                .param(someId)
                .query(rowMapper)
                .optional();
    }

    public Stream<TradingPostData> all() {
        String statement = "SELECT * FROM trading_post";

        TableConfigRowMapper<TradingPostData> rowMapper = this.rowMapper(TradingPostData.builder()::build);
        return this.jdbcClient.sql(statement)
                .query(rowMapper)
                .stream();
    }
}

