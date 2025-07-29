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

import java.util.NoSuchElementException;

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

        var caravanName = "Tour de France";
        var myCaravan = Caravans.create(CaravanData.builder()
                .name(caravanName)
//                .destinationId(myTradePost.getId())
                .build()
        );
        System.out.println(myCaravan);
        try {
            var foundCaravan = Caravans.getByName(caravanName);
            var caravanDest = foundCaravan.getDestination();
            Caravans.delete(myCaravan);
            caravanDest.ifPresent(TradingPosts::delete);
        } catch (NoSuchElementException e) {
            throw new RuntimeException(e);
        } finally {
            Caravans.delete(myCaravan);
            TradingPosts.delete(myTradePost);
        }
    }
}
