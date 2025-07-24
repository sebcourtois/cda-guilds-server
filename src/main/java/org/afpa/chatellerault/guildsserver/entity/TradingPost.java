package org.afpa.chatellerault.guildsserver.entity;

import java.util.UUID;

@lombok.Data
@lombok.Builder
@lombok.AllArgsConstructor
public class TradingPost {
    @lombok.NonNull
    UUID id;
    @lombok.NonNull
    String name;

    @lombok.Builder.Default
    int posX = 0;
    @lombok.Builder.Default
    int posY = 0;
    @lombok.Builder.Default
    int population = 0;

    UUID hostId;
}
