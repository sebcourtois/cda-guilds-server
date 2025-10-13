package org.afpa.chatellerault.guildsserver.service;

import org.afpa.chatellerault.guildsserver.azgaarworld.AzWorld;
import org.afpa.chatellerault.guildsserver.model.BiomeData;
import org.afpa.chatellerault.guildsserver.model.MapTileData;
import org.afpa.chatellerault.guildsserver.model.TradingPostData;
import org.afpa.chatellerault.guildsserver.repository.BiomeRepository;
import org.afpa.chatellerault.guildsserver.repository.MapTileRepository;
import org.afpa.chatellerault.guildsserver.repository.TradingPostRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.io.InputStream;

@SpringBootTest
class AzgaarImporterTest {
    private static final Logger LOG = LogManager.getLogger(AzgaarImporterTest.class);

    private final JdbcClient jdbcClient;

    @Autowired
    public AzgaarImporterTest(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
        Biomes.setRepository(new BiomeRepository(this.jdbcClient));
        MapTiles.setRepository(new MapTileRepository(this.jdbcClient));
        TradingPosts.setRepository(new TradingPostRepository(this.jdbcClient));
    }

    @Test
    void importWorld() throws Exception {
        InputStream jsonStream = new ClassPathResource("azgaar_world.json").getInputStream();
        AzWorld azWorld = AzWorld.fromJson(jsonStream);
        AzgaarImporter.importWorld(azWorld);
    }

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(this.jdbcClient,
                TradingPostData.TradingPostTable.name,
                MapTileData.MapTileTable.name,
                BiomeData.BiomeTable.name
        );
    }
}