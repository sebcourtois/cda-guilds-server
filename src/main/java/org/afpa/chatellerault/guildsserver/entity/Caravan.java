package org.afpa.chatellerault.guildsserver.entity;

import java.util.UUID;

@lombok.Data
@lombok.Builder
public class Caravan {
    UUID id;
    String name;

    @lombok.Builder.Default
    int posX = 0;
    @lombok.Builder.Default
    int posY = 0;

    TradingPost destination;
}
