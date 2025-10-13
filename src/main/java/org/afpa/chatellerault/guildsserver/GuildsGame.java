package org.afpa.chatellerault.guildsserver;

import org.afpa.chatellerault.guildsserver.core.GuildsDateProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuildsGame {
    private static final Logger LOG = LogManager.getLogger(GuildsGame.class);
    private final GuildsDateProvider dateProvider;
    private boolean running;

    public GuildsGame(GuildsDateProvider dateProvider) {
        this.dateProvider = dateProvider;
        this.running = false;
    }

    public void run() {
        this.running = true;

        while (this.running) {
            var date = this.dateProvider.nextDate();
            System.out.println(date);
        }
        LOG.info("Game loop exited");
    }
}
