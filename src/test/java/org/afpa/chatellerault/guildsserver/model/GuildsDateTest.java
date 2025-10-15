package org.afpa.chatellerault.guildsserver.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GuildsDateTest {

    @Test
    void sameDatesAreEquals() {
        GuildsDate d1 = GuildsDate.builder().year(10).day(55).build();
        GuildsDate d2 = GuildsDate.builder().year(10).day(55).build();
        assertThat(d1.equals(d2)).isTrue();
    }

    @Test
    void sameDatesWithDifferentSourcesAreEquals() {
        GuildsDate d1 = GuildsDate.builder().year(10).day(55).source("source1").build();
        GuildsDate d2 = GuildsDate.builder().year(10).day(55).source("source2").build();
        assertThat(d1.equals(d2)).isTrue();
    }

    @Test
    void twoDatesWithOneYearGap() {
        GuildsDate d1 = GuildsDate.builder().year(10).day(55).source("source1").build();
        GuildsDate d2 = GuildsDate.builder().year(11).day(55).source("source2").build();
        assertThat(d1.elapsedDays(d2)).isEqualTo(365);
        assertThat(d2.elapsedDays(d1)).isEqualTo(-365);
    }

    @Test
    void twoDatesWithTenDaysGap() {
        GuildsDate d1 = GuildsDate.builder().year(10).day(55).source("source1").build();
        GuildsDate d2 = GuildsDate.builder().year(10).day(65).source("source2").build();
        assertThat(d1.elapsedDays(d2)).isEqualTo(10);
        assertThat(d2.elapsedDays(d1)).isEqualTo(-10);
    }

    @Test
    void twoDatesWithFiveYearsAndThirtyDaysGap() {
        GuildsDate d1 = GuildsDate.builder().year(15).day(50).source("source1").build();
        GuildsDate d2 = GuildsDate.builder().year(20).day(80).source("source2").build();

        int elapsedDays = d1.elapsedDays(d2);
        assertThat(elapsedDays % 365).isEqualTo(30);
        assertThat(elapsedDays / 365).isEqualTo(5);
    }
}