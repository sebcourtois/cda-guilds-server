package org.afpa.chatellerault.guildsserver.repository;

import lombok.NonNull;
import org.afpa.chatellerault.guildsserver.entity.TradingPost;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.lang.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;


public class TradingPostRepository {

    JdbcClient jdbcClient;
    TradingPostRowMapper rowMapper = new TradingPostRowMapper();

    public TradingPostRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public static TradingPost mapRow(@NonNull ResultSet row, @Nullable String columnPrefix) throws SQLException {
        columnPrefix = (columnPrefix == null) ? "" : columnPrefix;
        return TradingPost.builder()
                .id(row.getObject(columnPrefix + "id", UUID.class))
                .name(row.getString(columnPrefix + "name"))
                .posX(row.getInt(columnPrefix + "location_x"))
                .posY(row.getInt(columnPrefix + "location_y"))
                .population(row.getInt(columnPrefix + "population"))
                .hostId(row.getObject(columnPrefix + "id_host", UUID.class))
                .build();
    }

    public static TradingPost mapRow(@NonNull ResultSet row) throws SQLException {
        return TradingPostRepository.mapRow(row, null);
    }

    public TradingPost create(TradingPost tradingPost) {
        String statement = """
                INSERT INTO trading_post (name, location_x, location_y, population, id_host)
                VALUES (?, ?, ?, ?, ?) RETURNING *;
                """;

        return this.jdbcClient.sql(statement)
                .param(tradingPost.getName())
                .param(tradingPost.getPosX())
                .param(tradingPost.getPosY())
                .param(tradingPost.getPopulation())
                .param(tradingPost.getHostId())
                .query(rowMapper).single();
    }

    public UUID delete(TradingPost tradingPost) {
        String statement = """
                DELETE FROM trading_post WHERE id = ? RETURNING id;
                """;
        return this.jdbcClient.sql(statement).param(tradingPost.getId()).query(UUID.class).single();
    }

    public Optional<TradingPost> findByName(String someName) {
        String statement = "SELECT * FROM trading_post WHERE name = ?";

        return this.jdbcClient.sql(statement)
                .param(someName)
                .query(rowMapper).optional();
    }

    public Optional<TradingPost> findById(UUID someId) {
        String statement = "SELECT * FROM trading_post WHERE id = ?";

        return this.jdbcClient.sql(statement)
                .param(someId)
                .query(rowMapper).optional();
    }
}

class TradingPostRowMapper implements RowMapper<TradingPost> {

    @Override
    public TradingPost mapRow(@NonNull ResultSet row, int rowNum) throws SQLException {
        return TradingPostRepository.mapRow(row);
    }
}
