package org.afpa.chatellerault.guildsserver.repository;

import org.afpa.chatellerault.guildsserver.model.Caravan;
import org.afpa.chatellerault.guildsserver.util.BaseRepository;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.Optional;

public class CaravanRepository extends BaseRepository<Caravan> {

    public CaravanRepository(JdbcClient jdbcClient) {
        super(jdbcClient);
    }

    public Optional<Caravan> findByName(String caravanName) {
        String statement = """
                SELECT * FROM "caravan" WHERE "name" = ?;
                """;

        return this.jdbcClient.sql(statement)
                .param(caravanName)
                .query(this.entityRowMapper(Caravan.builder()::build))
                .optional();
    }
}