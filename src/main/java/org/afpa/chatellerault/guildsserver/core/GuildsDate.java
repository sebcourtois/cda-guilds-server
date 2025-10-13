package org.afpa.chatellerault.guildsserver.core;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GuildsDate {
    @lombok.Builder.Default
    String source = "undefined";
    @lombok.Builder.Default
    int year = 0;
    @lombok.Builder.Default
    int day = 0;
}
