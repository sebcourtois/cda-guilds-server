package org.afpa.chatellerault.guildsserver.service;

import org.afpa.chatellerault.guildsserver.azgaarworld.AzWorld;
import org.afpa.chatellerault.guildsserver.model.BiomeData;
import org.afpa.chatellerault.guildsserver.model.CaravanData;
import org.afpa.chatellerault.guildsserver.model.MapCaseData;
import org.afpa.chatellerault.guildsserver.model.TradingPostData;
import org.afpa.chatellerault.guildsserver.repository.BiomeRepository;
import org.afpa.chatellerault.guildsserver.repository.MapCaseRepository;
import org.afpa.chatellerault.guildsserver.repository.TradingPostRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    }

    @Test
    void importWorld() throws Exception {
        JdbcTestUtils.deleteFromTables(this.jdbcClient,
                CaravanData.builder().build().tableName(),
                TradingPostData.builder().build().tableName(),
                MapCaseData.builder().build().tableName(),
                BiomeData.builder().build().tableName()
        );
        Biomes.setRepository(new BiomeRepository(this.jdbcClient));
        MapCases.setRepository(new MapCaseRepository(this.jdbcClient));
        TradingPosts.setRepository(new TradingPostRepository(this.jdbcClient));

        InputStream jsonStream = new ClassPathResource("azgaar_world.json").getInputStream();
        AzWorld azWorld = AzWorld.fromJson(jsonStream);
        AzgaarImporter.importWorld(azWorld);
    }
}