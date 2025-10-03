package org.afpa.chatellerault.guildsserver;

import org.afpa.chatellerault.guildsserver.model.*;
import org.afpa.chatellerault.guildsserver.model.azgaarworld.AzBiome;
import org.afpa.chatellerault.guildsserver.model.azgaarworld.AzBurg;
import org.afpa.chatellerault.guildsserver.model.azgaarworld.AzPackCell;
import org.afpa.chatellerault.guildsserver.model.azgaarworld.AzWorld;
import org.afpa.chatellerault.guildsserver.repository.BiomeRepository;
import org.afpa.chatellerault.guildsserver.repository.CaravanRepository;
import org.afpa.chatellerault.guildsserver.repository.MapCaseRepository;
import org.afpa.chatellerault.guildsserver.repository.TradingPostRepository;
import org.afpa.chatellerault.guildsserver.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GuildsServerAppTests {
    private static final Logger LOG = LogManager.getLogger(GuildsServerAppTests.class);

    private final JdbcClient jdbcClient;

    @Autowired
    public GuildsServerAppTests(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @BeforeEach
    void clearTables() {
        JdbcTestUtils.deleteFromTables(this.jdbcClient,
                CaravanData.builder().build().tableName(),
                TradingPostData.builder().build().tableName(),
                MapCaseData.builder().build().tableName(),
                BiomeData.builder().build().tableName()
        );
    }

    @Test
    void createOneCaravanWithDestinationAndDeleteIt() throws Exception {
        TradingPosts.setRepository(new TradingPostRepository(this.jdbcClient));
        Caravans.setRepository(new CaravanRepository(this.jdbcClient));

        TradingPost someTradePost = TradingPosts.create(TradingPostData.builder()
                .name("Chatellerault")
                .build()
        );
        LOG.info(someTradePost);

        assertThat(JdbcTestUtils.countRowsInTable(this.jdbcClient, someTradePost.getData().tableName())).isOne();

        String caravanName = "Tour de France";
        Caravan someCaravan = Caravans.create(CaravanData.builder()
                .name(caravanName)
                .destinationId(someTradePost.getId())
                .build()
        );
        LOG.info(someCaravan);

        assertThat(JdbcTestUtils.countRowsInTable(this.jdbcClient, someCaravan.getData().tableName())).isOne();

        try {
            Caravan foundCaravan = Caravans.getByName(caravanName);
            Optional<TradingPost> caravanDest = foundCaravan.getDestination();
            Caravans.delete(someCaravan);
            caravanDest.ifPresent(TradingPosts::delete);
        } finally {
            Caravans.delete(someCaravan);
            TradingPosts.delete(someTradePost);
        }
    }

    @Test
    void populateDatabaseWithAzgaarWorld() throws Exception {
        TradingPosts.setRepository(new TradingPostRepository(this.jdbcClient));
        Biomes.setRepository(new BiomeRepository(this.jdbcClient));
        MapCases.setRepository(new MapCaseRepository(this.jdbcClient));

        InputStream jsonStream = new ClassPathResource("azgaar_world.json").getInputStream();
        AzWorld azWorld = AzgaarWorlds.loadFromJson(jsonStream);

        var biomes = new HashMap<Integer, Biome>(azWorld.biomes().size());
        for (Map.Entry<Integer, AzBiome> mapEntry : azWorld.biomes().entrySet()) {
            var azBiome = mapEntry.getValue();
            Biome biome = Biomes.create(BiomeData.builder()
                    .name(azBiome.name())
                    .build()
            );
            biomes.put(mapEntry.getKey(), biome);
        }

        var mapCases = new HashMap<Integer, MapCase>(azWorld.cells().size());
        for (Map.Entry<Integer, AzPackCell> mapEntry : azWorld.cells().entrySet()) {
            var azCell = mapEntry.getValue();
            MapCase mapCase = MapCases.create(MapCaseData.builder()
                    .posX((long) azCell.position()[0])
                    .posY((long) azCell.position()[1])
                    .biomeId(biomes.get(azCell.biomeId()).getId())
                    .build()
            );
            mapCases.put(mapEntry.getKey(), mapCase);
        }

        for (AzBurg azBurg : azWorld.burgs().values()) {
            TradingPosts.create(TradingPostData.builder()
                    .name(azBurg.name())
                    .population((int) azBurg.population() * 1000)
                    .mapCaseId(mapCases.get(azBurg.cellId()).getId())
                    .build()
            );
//            System.out.println(azBurg);
        }
    }
}
