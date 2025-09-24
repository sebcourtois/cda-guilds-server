package org.afpa.chatellerault.guildsserver;

import org.afpa.chatellerault.guildsserver.model.Caravan;
import org.afpa.chatellerault.guildsserver.model.CaravanData;
import org.afpa.chatellerault.guildsserver.model.TradingPost;
import org.afpa.chatellerault.guildsserver.model.TradingPostData;
import org.afpa.chatellerault.guildsserver.repository.CaravanRepository;
import org.afpa.chatellerault.guildsserver.repository.TradingPostRepository;
import org.afpa.chatellerault.guildsserver.service.Caravans;
import org.afpa.chatellerault.guildsserver.service.TradingPosts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.jdbc.JdbcTestUtils;

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
}
