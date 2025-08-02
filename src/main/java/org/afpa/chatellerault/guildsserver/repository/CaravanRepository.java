package org.afpa.chatellerault.guildsserver.repository;

import org.afpa.chatellerault.guildsserver.model.CaravanData;
import org.afpa.chatellerault.guildsserver.util.BaseRepository;
import org.afpa.chatellerault.guildsserver.util.TableMappedObj;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CaravanRepository extends BaseRepository {

    public CaravanRepository(JdbcClient jdbcClient) {
        super(jdbcClient);
    }

    public Optional<TableMappedObj> findByName(String caravanName) {
        String statement = """
                SELECT * FROM "caravan" WHERE "name" = ?;
                """;

        return this.jdbcClient.sql(statement)
                .param(caravanName)
                .query(this.rowMapper(CaravanData.builder()::build))
                .optional();
    }
}