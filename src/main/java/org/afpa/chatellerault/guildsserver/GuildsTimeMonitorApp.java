package org.afpa.chatellerault.guildsserver;

import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;

@Profile("!test")
@org.springframework.stereotype.Component
public class GuildsTimeMonitorApp implements ApplicationRunner {
    private static final Logger LOG = LogManager.getLogger(GuildsTimeMonitorApp.class);

    private GuildsTimeMonitor gtMonitor;

    @Override
    public void run(ApplicationArguments args) {
        this.gtMonitor = new GuildsTimeMonitor();
        Thread.ofPlatform().start(this.gtMonitor);
    }

    @PreDestroy
    public void stop() {
        if (this.gtMonitor == null) return;
        LOG.info("stopping {}...", this.gtMonitor.getClass().getSimpleName());
        this.gtMonitor.stop();
    }
}
