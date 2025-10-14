package org.afpa.chatellerault.guildsserver.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
public class GuildsDate {
    @EqualsAndHashCode.Exclude
    @lombok.Builder.Default
    String source = "undefined";

    @lombok.Builder.Default
    int year = 0;

    @lombok.Builder.Default
    int day = 0;

    public int elapsedDays(GuildsDate other) {
        return ((other.year - this.year) * 365) + (other.day - this.day);
    }
}
