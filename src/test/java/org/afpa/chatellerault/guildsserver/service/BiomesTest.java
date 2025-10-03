package org.afpa.chatellerault.guildsserver.service;

import org.afpa.chatellerault.guildsserver.model.Biome;
import org.afpa.chatellerault.guildsserver.model.BiomeData;
import org.afpa.chatellerault.guildsserver.repository.BiomeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.jdbc.JdbcTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BiomesTest {
    private final JdbcClient jdbcClient;

    @Autowired
    public BiomesTest(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @BeforeEach
    void setUp() {
        JdbcTestUtils.deleteFromTables(this.jdbcClient, BiomeData.builder().build().tableName());
        Biomes.setRepository(new BiomeRepository(this.jdbcClient));
    }

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(this.jdbcClient, BiomeData.builder().build().tableName());
    }

    @Test
    void create() throws Exception {
        var data = BiomeData.builder()
                .name("Wonderland")
                .build();
        Biome myBiome = Biomes.create(data);
        assertThat(myBiome.getId()).isNotNull();
    }
}