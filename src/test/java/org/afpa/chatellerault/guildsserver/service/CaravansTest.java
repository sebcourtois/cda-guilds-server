package org.afpa.chatellerault.guildsserver.service;

import org.afpa.chatellerault.guildsserver.model.Caravan;
import org.afpa.chatellerault.guildsserver.model.CaravanData;
import org.afpa.chatellerault.guildsserver.model.TradingPost;
import org.afpa.chatellerault.guildsserver.model.TradingPostData;
import org.afpa.chatellerault.guildsserver.repository.CaravanRepository;
import org.afpa.chatellerault.guildsserver.repository.TradingPostRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CaravansTest {
    private static final Logger LOG = LogManager.getLogger(CaravansTest.class);

    private final JdbcClient jdbcClient;

    @Autowired
    public CaravansTest(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
        TradingPosts.setRepository(new TradingPostRepository(this.jdbcClient));
        Caravans.setRepository(new CaravanRepository(this.jdbcClient));
    }

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(this.jdbcClient,
                CaravanData.CaravanTable.name,
                TradingPostData.TradingPostTable.name
        );
    }

    @Test
    void createOneCaravanWithDestinationAndDeleteThem() throws Exception {
        TradingPost someTradePost = TradingPosts.create(TradingPostData.builder()
                .name("Chatellerault")
                .build()
        );
        LOG.info(someTradePost);

        assertThat(JdbcTestUtils.countRowsInTable(this.jdbcClient, TradingPostData.TradingPostTable.name)).isOne();

        String caravanName = "Tour de France";
        Caravan someCaravan = Caravans.create(CaravanData.builder()
                .name(caravanName)
                .destinationId(someTradePost.getId())
                .build()
        );
        LOG.info(someCaravan);

        assertThat(JdbcTestUtils.countRowsInTable(this.jdbcClient, CaravanData.CaravanTable.name)).isOne();

        try {
            Caravan foundCaravan = Caravans.getByName(caravanName);
            Optional<TradingPost> caravanDest = foundCaravan.getDestination();
            Caravans.delete(someCaravan);
            caravanDest.ifPresent(tradingPost -> {
                try {
                    TradingPosts.delete(tradingPost);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } finally {
            Caravans.delete(someCaravan);
            TradingPosts.delete(someTradePost);
        }

        assertThat(JdbcTestUtils.countRowsInTable(
                this.jdbcClient, TradingPostData.TradingPostTable.name
        )).isZero();
        assertThat(JdbcTestUtils.countRowsInTable(
                this.jdbcClient, CaravanData.CaravanTable.name
        )).isZero();
    }
}
