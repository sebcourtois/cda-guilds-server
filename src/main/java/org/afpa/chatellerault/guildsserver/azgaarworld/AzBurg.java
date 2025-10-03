package org.afpa.chatellerault.guildsserver.azgaarworld;

public record AzBurg(
        int id,
        String name,
        double x,
        double y,
        double population,
        int cellId
        ) {
}
