package org.afpa.chatellerault.guildsserver.service;

import org.afpa.chatellerault.guildsserver.model.MapCase;
import org.afpa.chatellerault.guildsserver.model.MapCaseData;
import org.afpa.chatellerault.guildsserver.repository.MapCaseRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.jdbc.JdbcTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MapCasesTest {
    private final JdbcClient jdbcClient;

    @Autowired
    public MapCasesTest(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @BeforeEach
    void setUp() {
        JdbcTestUtils.deleteFromTables(this.jdbcClient, MapCaseData.builder().build().tableName());
        MapCases.setRepository(new MapCaseRepository(this.jdbcClient));
    }

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(this.jdbcClient, MapCaseData.builder().build().tableName());
    }

    @Test
    void create() throws Exception {
        var data = MapCaseData.builder()
                .posX(5487)
                .posY(5465)
                .build();
        MapCase myMapCase = MapCases.create(data);
        assertThat(myMapCase.getId()).isNotNull();
    }
}