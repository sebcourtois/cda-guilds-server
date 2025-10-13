package org.afpa.chatellerault.guildsserver;

import org.afpa.chatellerault.guildsserver.core.GuildsDate;
import org.afpa.chatellerault.guildsserver.core.GuildsDateProvider;

public class DevDateProvider implements GuildsDateProvider {
    private final int interval;
    private GuildsDate currentDate;

    public DevDateProvider(int startYear, int startDay, int interval) {
        this.interval = interval;
        this.currentDate = GuildsDate.builder()
                .year(startYear).day(startDay).source("local_dev")
                .build();
    }

    @Override
    public GuildsDate nextDate() {
        try {
            Thread.sleep(this.interval);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        int nextDay = this.currentDate.getDay() + 1;
        int nextYear = this.currentDate.getYear();
        if (nextDay > 365) {
            nextDay = 1;
            nextYear += 1;
        }
        this.currentDate = GuildsDate.builder()
                .source(this.currentDate.getSource())
                .day(nextDay).year(nextYear)
                .build();

        return  this.currentDate;
    }
}
