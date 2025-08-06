package org.afpa.chatellerault.guildsserver;

import org.afpa.chatellerault.guildsserver.model.CaravanData;
import org.afpa.chatellerault.guildsserver.model.HostServerData;
import org.afpa.chatellerault.guildsserver.model.TradingPostData;
import org.afpa.chatellerault.guildsserver.repository.CaravanRepository;
import org.afpa.chatellerault.guildsserver.repository.HostServerRepository;
import org.afpa.chatellerault.guildsserver.repository.TradingPostRepository;
import org.afpa.chatellerault.guildsserver.service.Caravans;
import org.afpa.chatellerault.guildsserver.service.HostServers;
import org.afpa.chatellerault.guildsserver.service.TradingPosts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.net.InetAddress;
import java.util.NoSuchElementException;

@SpringBootApplication
public class GuildsServerApp {
    private static final Logger LOG = LogManager.getLogger(GuildsServerApp.class);

    JdbcClient jdbcClient;

    public GuildsServerApp(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(GuildsServerApp.class, args);
    }

    public void populateDb(ApplicationArguments args) throws Exception {
        TradingPosts.setRepository(new TradingPostRepository(this.jdbcClient));
        Caravans.setRepository(new CaravanRepository(this.jdbcClient));
        HostServers.setRepository(new HostServerRepository(this.jdbcClient));

        var myHostServer = HostServers.create(HostServerData.builder()
                .ipAddress(InetAddress.getByName("127.0.0.1"))
                .port(55555)
                .build()
        );
        LOG.info(myHostServer);

        var someTradePost = TradingPosts.create(TradingPostData.builder()
                .name("Chatellerault")
                .hostId(myHostServer.getId())
                .build()
        );
        LOG.info(someTradePost);

        var caravanName = "Tour de France";
        var someCaravan = Caravans.create(CaravanData.builder()
                .name(caravanName)
                .destinationId(someTradePost.getId())
                .build()
        );
        LOG.info(someCaravan);
        try {
            var foundCaravan = Caravans.getByName(caravanName);
            var caravanDest = foundCaravan.getDestination();
            Caravans.delete(someCaravan);
            caravanDest.ifPresent(TradingPosts::delete);
        } catch (NoSuchElementException e) {
            throw new RuntimeException(e);
        } finally {
            Caravans.delete(someCaravan);
            TradingPosts.delete(someTradePost);
            HostServers.delete(myHostServer);
        }
    }
}
