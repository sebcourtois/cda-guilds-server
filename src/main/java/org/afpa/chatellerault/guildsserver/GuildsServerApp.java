package org.afpa.chatellerault.guildsserver;

import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.io.IOException;

@SpringBootApplication
public class GuildsServerApp implements ApplicationRunner {
    private static final Logger LOG = LogManager.getLogger(GuildsServerApp.class);
    private final GuildsServer guildsServer;
    private final JdbcClient jdbcClient;

    public GuildsServerApp(JdbcClient jdbcClient) throws IOException {
        this.jdbcClient = jdbcClient;
        this.guildsServer = new GuildsServer();

    }

    public static void main(String[] args) {
        SpringApplication.run(GuildsServerApp.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        LOG.info(this.jdbcClient);
        this.guildsServer.start();
    }

    @PreDestroy
    public void stop() {
        LOG.info("stopping {}...", this.guildsServer.getClass().getSimpleName());
        this.guildsServer.shutdown();
    }
}
