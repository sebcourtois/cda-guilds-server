package org.afpa.chatellerault.guildsserver.repository;

import lombok.NonNull;
import org.afpa.chatellerault.guildsserver.entity.Caravan;
import org.afpa.chatellerault.guildsserver.entity.TradingPost;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CaravanRepository {

    JdbcClient jdbcClient;
    CaravanRowMapper rowMapper = new CaravanRowMapper();

    public CaravanRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    static public Caravan mapRow(ResultSet row) throws SQLException {
        TradingPost destination = null;
        UUID destId = row.getObject("destination.id", UUID.class);
        if (destId != null) {
            destination = TradingPostRepository.mapRow(row, "destination.");
        }
        return Caravan.builder()
                .id(row.getObject("id", UUID.class))
                .name(row.getString("name"))
                .posX(row.getInt("location_x"))
                .posY(row.getInt("location_y"))
                .destination(destination)
                .build();
    }

    public Caravan create(Caravan caravan) {
        String statement = """
                INSERT INTO caravan (name, location_x, location_y, destination)
                VALUES (?, ?, ?, ?) RETURNING *;
                """;

        var destTradingPost = caravan.getDestination();

        return this.jdbcClient.sql(statement)
                .param(caravan.getName())
                .param(caravan.getPosX())
                .param(caravan.getPosY())
                .param(destTradingPost != null ? destTradingPost.getId() : null)
                .query(rowMapper).single();
    }

    public UUID delete(Caravan caravan) {
        String statement = """
                DELETE FROM caravan WHERE id = ? RETURNING id;
                """;
        return this.jdbcClient.sql(statement).param(caravan.getId()).query(UUID.class).single();
    }

    public Caravan findByName(String caravanName) {
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
                .query(rowMapper).single();
    }
}

class CaravanRowMapper implements RowMapper<Caravan> {

    @Override
    public Caravan mapRow(@NonNull ResultSet row, int rowNum) throws SQLException {
        return CaravanRepository.mapRow(row);
    }
}