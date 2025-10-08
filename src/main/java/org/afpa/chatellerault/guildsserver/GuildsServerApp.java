package org.afpa.chatellerault.guildsserver;

import jakarta.annotation.PreDestroy;
import org.afpa.chatellerault.guildsserver.azgaarworld.AzWorld;
import org.afpa.chatellerault.guildsserver.repository.*;
import org.afpa.chatellerault.guildsserver.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

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
    public void run(ApplicationArguments args) throws Exception {
        Biomes.setRepository(new BiomeRepository(this.jdbcClient));
        Caravans.setRepository(new CaravanRepository(this.jdbcClient));
        HostServers.setRepository(new HostServerRepository(this.jdbcClient));
        MapTiles.setRepository(new MapTileRepository(this.jdbcClient));
        TradingPosts.setRepository(new TradingPostRepository(this.jdbcClient));

        Optional<Path> worldFilePath = singleOptionValueFromArgs(args, "import-world");
        if (worldFilePath.isPresent()) {
            if (MapTiles.getRowCount() > 0) {
                LOG.error("A world already exists. Destroy it, first.");
                System.exit(1);
            }
            AzWorld azWorld = AzWorld.fromJson(Files.newInputStream(worldFilePath.get()));
            AzgaarImporter.importWorld(azWorld);
        }

        this.guildsServer.start();
    }

    static private Optional<Path> singleOptionValueFromArgs(ApplicationArguments args, String optionName) {
        if (!args.containsOption(optionName)) return Optional.empty();
        List<String> optionValues = args.getOptionValues(optionName);
        if (optionValues.isEmpty()) {
            String msg = "No value passed to command-line option: '%s'".formatted(optionName);
            throw new RuntimeException(msg);
        }
        if (optionValues.size() > 1 ) {
            String msg = "More than one value passed to command-line option: '%s'".formatted(optionName);
            throw new RuntimeException(msg);
        }
        return Optional.of(Path.of(optionValues.getFirst()));
    }

    @PreDestroy
    public void stop() {
        LOG.info("stopping {}...", this.guildsServer.getClass().getSimpleName());
        this.guildsServer.shutdown();
    }
}
