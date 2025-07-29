package org.afpa.chatellerault.guildsserver;

import org.afpa.chatellerault.guildsserver.model.CaravanData;
import org.afpa.chatellerault.guildsserver.model.TradingPostData;
import org.afpa.chatellerault.guildsserver.repository.CaravanRepository;
import org.afpa.chatellerault.guildsserver.repository.TradingPostRepository;
import org.afpa.chatellerault.guildsserver.service.Caravans;
import org.afpa.chatellerault.guildsserver.service.TradingPosts;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.simple.JdbcClient;

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
        TradingPosts.setRepository(new TradingPostRepository(this.jdbcClient));
        Caravans.setRepository(new CaravanRepository(this.jdbcClient));

        var myTradePost = TradingPosts.create(TradingPostData.builder()
                .name("Chatellerault")
                .build()
        );
        System.out.println(myTradePost);

        var myCaravan = Caravans.create(CaravanData.builder()
                .name("Tour de France")
                .destination(myTradePost.data)
                .build()
        );
        System.out.println(myCaravan);

        Caravans.delete(myCaravan);
        TradingPosts.delete(myTradePost);
    }

    private void _populateDatabase() {
        var tpRepo = new TradingPostRepository(this.jdbcClient);
        var carRepo = new CaravanRepository(this.jdbcClient);
//        var tradingPosts = new ArrayList<TradingPost>();

        int caravanCount = 0;
        int tradePostCount = 0;
        for (var tp = 0; tp < 4; tp++) {
            var tpName = String.format("Trading Post %s", tradePostCount + 1);
            var tradingPost = TradingPostData.builder()
                    .name(tpName)
                    .hostId(UUID.fromString("7b1f7a60-4eec-43a7-aad8-23db0edcfa07"))
                    .build();
            tpRepo.create(tradingPost);
            tradePostCount++;
//            tradingPosts.add(tradingPost);

            for (var car = 0; car < 3; car++) {
                var caravanName = String.format("Caravan %s", caravanCount + 1);
                var caravan = CaravanData.builder()
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
