package org.afpa.chatellerault.guildsserver.repository;

import lombok.NonNull;
import org.afpa.chatellerault.guildsserver.entity.TradingPost;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.lang.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;


public class TradingPostRepository extends BaseRepository<TradingPost> {

    public TradingPostRepository(JdbcClient jdbcClient) {
        super(jdbcClient);
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

    public Optional<TradingPost> findByName(String someName) {
        String statement = "SELECT * FROM trading_post WHERE name = ?";

        return this.jdbcClient.sql(statement)
                .param(someName)
                .query(this).optional();
    }

    public Optional<TradingPost> findById(UUID someId) {
        String statement = "SELECT * FROM trading_post WHERE id = ?";

        return this.jdbcClient.sql(statement)
                .param(someId)
                .query(this).optional();
    }

    @Override
    public TradingPost mapRow(@NonNull ResultSet res, int rowNum) throws SQLException {
        return TradingPostRepository.mapRow(res, null);
    }
}

