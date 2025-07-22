package org.afpa.chatellerault.guildsserver.repository;

import lombok.NonNull;
import org.afpa.chatellerault.guildsserver.entity.TradingPost;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


public class TradingPostRepository {

    JdbcClient jdbcClient;

    public TradingPostRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public TradingPost create(TradingPost trading_post) {
        String statement = """
                INSERT INTO trading_post (name, location_x, location_y, population)
                VALUES (?, ?, ?, ?) RETURNING *;
                """;
        return this.jdbcClient.sql(statement)
                .param(trading_post.getName())
                .param(trading_post.getPosX())
                .param(trading_post.getPosY())
                .param(trading_post.getPopulation())
                .query(new TradingPostRowMapper()).single();
    }

    public TradingPost findByName(String someName) {
        String statement = "SELECT * FROM trading_post WHERE name = ?";

        return this.jdbcClient.sql(statement)
                .param(someName)
                .query(new TradingPostRowMapper()).single();
    }

    public TradingPost findById(UUID someId) {
        String statement = "SELECT * FROM trading_post WHERE id = ?";

        return this.jdbcClient.sql(statement)
                .param(someId)
                .query(new TradingPostRowMapper()).single();
    }

    public UUID delete(TradingPost tradingPost) {
        String statement = """
                DELETE FROM trading_post WHERE id = ? RETURNING id;
                """;
        return this.jdbcClient.sql(statement).param(tradingPost.getId()).query(UUID.class).single();
    }
}

class TradingPostRowMapper implements RowMapper<TradingPost> {

    @Override
    public TradingPost mapRow(@NonNull ResultSet row, int rowNum) throws SQLException {
        return TradingPost.fromTableRow(row);
    }
}
