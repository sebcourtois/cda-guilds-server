package org.afpa.chatellerault.guildsserver.repository;

import org.afpa.chatellerault.guildsserver.model.CaravanData;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.Optional;

public class CaravanRepository extends BaseRepository<CaravanData> {

    public CaravanRepository(JdbcClient jdbcClient) {
        super(jdbcClient);
    }

    public Optional<CaravanData> findByName(String caravanName) {
        String statement = """
                SELECT * FROM "caravan" WHERE "name" = ?;
                """;

        return this.jdbcClient.sql(statement)
                .param(caravanName)
                .query(this.entityRowMapper(CaravanData.builder()::build))
                .optional();
    }
}