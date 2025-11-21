package org.afpa.chatellerault.guildsserver.service;

import org.afpa.chatellerault.guildsserver.GuildsTimeClient;
import org.afpa.chatellerault.guildsserver.core.GuildsDateProvider;
import org.afpa.chatellerault.guildsserver.model.GuildsDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class OnlineDateProvider implements GuildsDateProvider {
    private static final Logger LOG = LogManager.getLogger(OnlineDateProvider.class);
    private final GuildsTimeClient gtClient;
    private GuildsDate currentDate;

    public OnlineDateProvider(GuildsTimeClient gtClient) throws IOException {
        this.gtClient = gtClient;
        this.currentDate = gtClient.receiveDate();
    }

    @Override
    public GuildsDate nextDate() {
        while (true) {
            GuildsDate nextDate;
            try {
                nextDate = this.gtClient.receiveDate();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (this.currentDate.elapsedDays(nextDate) <= 0) {
                LOG.error("received date not greater than current one");
                continue;
            }

            this.currentDate = nextDate;
            return this.currentDate;
        }
    }
}
