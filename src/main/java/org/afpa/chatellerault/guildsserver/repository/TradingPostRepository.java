package org.afpa.chatellerault.guildsserver.repository;

import org.afpa.chatellerault.guildsserver.entity.TradingPost;
import org.afpa.chatellerault.guildsserver.util.BaseRepository;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.Optional;
import java.util.UUID;


public class TradingPostRepository extends BaseRepository<TradingPost> {

    public TradingPostRepository(JdbcClient jdbcClient) {
        super(jdbcClient);
    }

    public Optional<TradingPost> findByName(String someName) {
        String statement = "SELECT * FROM trading_post WHERE name = ?";

        return this.jdbcClient.sql(statement)
                .param(someName)
                .query(this.entityRowMapper(TradingPost.builder()::build))
                .optional();
    }

    public Optional<TradingPost> findById(UUID someId) {
        String statement = "SELECT * FROM trading_post WHERE id = ?";

        var rowMapper = this.entityRowMapper(TradingPost.builder()::build);
        return this.jdbcClient.sql(statement)
                .param(someId)
                .query(rowMapper)
                .optional();
    }
}

