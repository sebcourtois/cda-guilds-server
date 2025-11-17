package org.afpa.chatellerault.guildsserver.repository;

import org.afpa.chatellerault.guildsserver.model.CaravanData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CaravanRepositoryTest {
    private final JdbcClient jdbcClient;
    private final CaravanRepository repository;

    @Autowired
    public CaravanRepositoryTest(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
        this.repository = new CaravanRepository(this.jdbcClient);
    }

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(this.jdbcClient, this.repository.tableConfig().name());
    }

    @Test
    void create() throws SQLException {
        int caravanCount = 5;
        List<CaravanData> newCaravans = IntStream.rangeClosed(1, caravanCount)
                .mapToObj("Caravan #%s"::formatted)
                .map(n -> CaravanData.builder().name(n).build())
                .toList();

        for (var caravanData : newCaravans) {
            this.repository.create(caravanData);
        }

        String tableName = this.repository.tableConfig().name();
        assertThat(JdbcTestUtils.countRowsInTable(this.jdbcClient, tableName)).isEqualTo(caravanCount);

        List<CaravanData> foundCaravans = this.repository.findAll().toList();

        Set<String> createdNames = newCaravans.stream()
                .map(CaravanData::getName)
                .collect(Collectors.toUnmodifiableSet());

        Set<String> retrievedNames = foundCaravans.stream()
                .map(CaravanData::getName)
                .collect(Collectors.toUnmodifiableSet());

        assertThat(retrievedNames).isEqualTo(createdNames);
    }
}