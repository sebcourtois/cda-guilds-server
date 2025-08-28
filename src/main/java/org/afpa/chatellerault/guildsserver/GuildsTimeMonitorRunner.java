package org.afpa.chatellerault.guildsserver;

import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;

@Profile("!test")
@org.springframework.stereotype.Component
public class GuildsTimeMonitorRunner implements ApplicationRunner {
    private static final Logger LOG = LogManager.getLogger(GuildsTimeMonitorRunner.class);

    private GuildsTimeMonitor monitor;
    private Thread monitorThread;

    @Override
    public void run(ApplicationArguments args) {
        this.monitor = new GuildsTimeMonitor();
        this.monitorThread = new Thread(this.monitor);
        this.monitorThread.start();
    }

    @PreDestroy
    public void stop() {
        LOG.info("stopping GuildsTimeMonitor...");
        this.monitor.stop();
        try {
            this.monitorThread.join();
        } catch (InterruptedException e) {
            LOG.info("GuildsTimeMonitor thread already interrupted");
        }
    }
}
