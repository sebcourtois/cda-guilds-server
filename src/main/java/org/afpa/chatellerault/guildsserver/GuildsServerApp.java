package org.afpa.chatellerault.guildsserver;

import org.afpa.chatellerault.guildsserver.entity.Caravan;
import org.afpa.chatellerault.guildsserver.entity.TradingPost;
import org.afpa.chatellerault.guildsserver.repository.CaravanRepository;
import org.afpa.chatellerault.guildsserver.repository.TradingPostRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.ArrayList;
import java.util.UUID;

@SpringBootApplication
public class GuildsServerApp implements ApplicationRunner {

    JdbcClient jdbcClient;

    public GuildsServerApp(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(GuildsServerApp.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var carRepo = new CaravanRepository(this.jdbcClient);
        var found = carRepo.findByName("Caravan 2");
        System.out.println(found.orElse(null));
    }

    private void _populateDatabase() {
        var tpRepo = new TradingPostRepository(this.jdbcClient);
        var carRepo = new CaravanRepository(this.jdbcClient);
//        var tradingPosts = new ArrayList<TradingPost>();

        int caravanCount = 0;
        int tradePostCount = 0;
        for (var tp = 0; tp < 4; tp++) {
            var tpName = String.format("Trading Post %s", tradePostCount + 1);
            var tradingPost = TradingPost.builder()
                    .name(tpName)
                    .hostId(UUID.fromString("7b1f7a60-4eec-43a7-aad8-23db0edcfa07"))
                    .build();
            tradingPost = tpRepo.create(tradingPost);
            tradePostCount++;
//            tradingPosts.add(tradingPost);

            for (var car = 0; car < 3; car++) {
                var caravanName = String.format("Caravan %s", caravanCount + 1);
                var caravan = Caravan.builder()
                        .name(caravanName)
                        .destination(tradingPost)
                        .build();
                carRepo.create(caravan);
                caravanCount++;
            }
        }

//        for (var tradePost : tradingPosts) {
//            var deletedId = tpRepo.delete(tradePost);
//            System.out.println(deletedId);
//        }
    }

}
