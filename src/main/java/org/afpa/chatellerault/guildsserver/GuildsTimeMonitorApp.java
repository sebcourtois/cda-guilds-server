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
    private Thread gtMonitorThread;

    @Override
    public void run(ApplicationArguments args) {
        this.gtMonitor = new GuildsTimeMonitor();
        this.gtMonitorThread = Thread.ofPlatform().start(this.gtMonitor);
    }

    @PreDestroy
    public void stop() {
        LOG.info("stopping {}...", this.gtMonitor.getClass().getSimpleName());
        this.gtMonitor.stop();
        try {
            this.gtMonitorThread.join();
        } catch (InterruptedException e) {
            LOG.info("{} thread already interrupted", this.gtMonitor.getClass().getSimpleName());
        }
    }
}
