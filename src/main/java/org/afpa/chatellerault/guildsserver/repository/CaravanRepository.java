package org.afpa.chatellerault.guildsserver.repository;

import lombok.NonNull;
import org.afpa.chatellerault.guildsserver.entity.Caravan;
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
        return Caravan.builder()
                .id(res.getObject("id", UUID.class))
                .name(res.getString("name"))
                .posX(res.getInt("location_x"))
                .posY(res.getInt("location_y"))
                .destinationId(res.getObject("id_destination", UUID.class))
                .build();
    }

    public Optional<Caravan> findByName(String caravanName) {
        String statement = """
                SELECT * FROM "caravan" WHERE "name" = ?;
                """;

        return this.jdbcClient.sql(statement)
                .param(caravanName)
                .query(this).optional();
    }
}