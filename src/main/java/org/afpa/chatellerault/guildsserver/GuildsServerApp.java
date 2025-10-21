package org.afpa.chatellerault.guildsserver;

import jakarta.annotation.PreDestroy;
import org.afpa.chatellerault.guildsserver.azgaarworld.AzWorld;
import org.afpa.chatellerault.guildsserver.core.GuildsDateProvider;
import org.afpa.chatellerault.guildsserver.core.RequestCommands;
import org.afpa.chatellerault.guildsserver.repository.*;
import org.afpa.chatellerault.guildsserver.service.*;
import org.afpa.chatellerault.guildsserver.util.AppArgs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

@SpringBootApplication
public class GuildsServerApp implements ApplicationRunner {
    private static final Logger LOG = LogManager.getLogger(GuildsServerApp.class);

    private final JdbcClient jdbcClient;
    private final GuildsServer guildsServer;
    private final GuildsTimeMonitor timeMonitor;
    private GuildsTimeClient timeClient;

    public GuildsServerApp(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
        this.guildsServer = new GuildsServer();
        this.timeMonitor = new GuildsTimeMonitor();
        this.timeClient = null;
    }

    public static void main(String[] args) {
        registerRequestCommands();
        SpringApplication.run(GuildsServerApp.class, args);
    }

    private static void registerRequestCommands() {
          RequestCommands.register("echo", SimpleEchoCmd::new);
    }

    @Override
    public void run(ApplicationArguments srcArgs) throws Exception {
        var args = new AppArgs(srcArgs);

        Biomes.setRepository(new BiomeRepository(this.jdbcClient));
        Caravans.setRepository(new CaravanRepository(this.jdbcClient));
        HostServers.setRepository(new HostServerRepository(this.jdbcClient));
        MapTiles.setRepository(new MapTileRepository(this.jdbcClient));
        TradingPosts.setRepository(new TradingPostRepository(this.jdbcClient));

        System.out.println(Arrays.toString(args.getSourceArgs()));

        Optional<Path> worldFilePath = args.singlePathForOption("import-world");
        if (worldFilePath.isPresent()) {
            if (MapTiles.getRowCount() > 0) {
                LOG.error("A world already exists. Destroy it, first.");
                System.exit(1);
            }
            AzWorld azWorld = AzWorld.fromJson(Files.newInputStream(worldFilePath.get()));
            AzgaarImporter.importWorld(azWorld);
        }

        if (!args.containsOption("run")) {
            return;
        }

        GuildsDateProvider dateProvider;
        boolean offline = args.containsOption("offline");
        if (offline) {
            dateProvider = new OfflineDateProvider(2001, 11, 2000);
        } else {
            this.timeClient = new GuildsTimeClient(null);
            dateProvider = new OnlineDateProvider(this.timeClient);
        }

        this.timeMonitor.start();
        this.guildsServer.start();

        var game = new GuildsGame(dateProvider);
//        game.run();
    }

    @PreDestroy
    public void stop() {
        if (this.timeClient != null) {
            this.timeClient.shutdown();
        }

        if (this.timeMonitor.isRunning()) {
            LOG.info("stopping {}...", this.timeMonitor.getClass().getSimpleName());
            this.timeMonitor.shutdown();
        }

        if (this.guildsServer.isRunning()) {
            LOG.info("stopping {}...", this.guildsServer.getClass().getSimpleName());
            this.guildsServer.shutdown();
        }
    }
}
