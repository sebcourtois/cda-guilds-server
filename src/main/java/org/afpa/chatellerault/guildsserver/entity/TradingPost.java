package org.afpa.chatellerault.guildsserver.entity;

import java.util.UUID;

@lombok.Data
@lombok.Builder
public class TradingPost {
    UUID id;
    String name;

    @lombok.Builder.Default
    int posX = 0;
    @lombok.Builder.Default
    int posY = 0;
    @lombok.Builder.Default
    int population = 0;
    @lombok.Builder.Default
    UUID hostId = null;
}
