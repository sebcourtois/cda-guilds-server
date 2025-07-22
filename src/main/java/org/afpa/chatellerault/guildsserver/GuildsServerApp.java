package org.afpa.chatellerault.guildsserver;

import org.afpa.chatellerault.guildsserver.entity.TradingPost;
import org.afpa.chatellerault.guildsserver.repository.TradingPostRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.ArrayList;

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
        var repo = new TradingPostRepository(this.jdbcClient);
        var tradingPosts = new ArrayList<TradingPost>();

        for (var i = 1; i <= 10; i++) {
            var name = String.format("Trading Post %s", i);
            var tradingPost = TradingPost.builder().name(name).build();
            var newTradePost = repo.create(tradingPost);
//            System.out.println(newTradePost);
            tradingPosts.add(newTradePost);
        }

        var found = repo.findByName("Trading Post 5");
        System.out.println(found);

        for (var tradePost : tradingPosts) {
            System.out.println(tradePost);
            var deletedId = repo.delete(tradePost);
            System.out.println(deletedId);
        }
    }
}
