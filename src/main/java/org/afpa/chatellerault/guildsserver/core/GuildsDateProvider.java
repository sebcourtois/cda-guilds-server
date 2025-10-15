package org.afpa.chatellerault.guildsserver.core;

import org.afpa.chatellerault.guildsserver.model.GuildsDate;

public interface GuildsDateProvider {
    GuildsDate nextDate();
}
