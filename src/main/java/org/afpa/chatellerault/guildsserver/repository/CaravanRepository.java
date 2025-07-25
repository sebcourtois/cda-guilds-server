package org.afpa.chatellerault.guildsserver.repository;

import lombok.NonNull;
import org.afpa.chatellerault.guildsserver.entity.Caravan;
import org.afpa.chatellerault.guildsserver.entity.TradingPost;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class CaravanRepository extends BaseRepository<Caravan> {

    public CaravanRepository(JdbcClient jdbcClient) {
        super(jdbcClient);
    }

    @Override
    public Caravan mapRow(@NonNull ResultSet res, int rowNum) throws SQLException {
        UUID destId;
        try {
            destId = res.getObject("destination.id", UUID.class);
        } catch (SQLException e) {
            destId = null;
        }
        TradingPost destination = null;
        if (destId != null) {
            destination = TradingPostRepository.mapRow(res, "destination.");
        }
        return Caravan.builder()
                .id(res.getObject("id", UUID.class))
                .name(res.getString("name"))
                .posX(res.getInt("location_x"))
                .posY(res.getInt("location_y"))
                .destination(destination)
                .build();
    }

    public Optional<Caravan> findByName(String caravanName) {
        String statement = """
                SELECT c1.id,
                       c1.name,
                       c1.location_x,
                       c1.location_y,
                       trading_post.id         AS "destination.id",
                       trading_post.name       AS "destination.name",
                       trading_post.location_x AS "destination.location_x",
                       trading_post.location_y AS "destination.location_y",
                       trading_post.population AS "destination.population",
                       trading_post.id_host    AS "destination.id_host"
                FROM (SELECT * FROM caravan WHERE caravan.name = ?) AS c1
                         LEFT JOIN trading_post ON c1.destination = trading_post.id
                """;

        return this.jdbcClient.sql(statement)
                .param(caravanName)
                .query(this).optional();
    }
}