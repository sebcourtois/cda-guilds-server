package org.afpa.chatellerault.guildsserver.service;

import org.afpa.chatellerault.guildsserver.model.MapTile;
import org.afpa.chatellerault.guildsserver.model.MapTileData;
import org.afpa.chatellerault.guildsserver.repository.MapTileRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.jdbc.JdbcTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MapTilesTest {
    private final JdbcClient jdbcClient;

    @Autowired
    public MapTilesTest(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @BeforeEach
    void setUp() {
        MapTiles.setRepository(new MapTileRepository(this.jdbcClient));
    }

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(this.jdbcClient, MapTileData.MapTileTable.name);
    }

    @Test
    void create() throws Exception {
        MapTileData data = MapTileData.builder()
                .posX(5487)
                .posY(5465)
                .build();
        MapTile myMapTile = MapTiles.create(data);

        assertThat(myMapTile.getId()).isNotNull();
        assertThat(JdbcTestUtils.countRowsInTable(
                this.jdbcClient, MapTileData.MapTileTable.name)).isOne();
    }
}