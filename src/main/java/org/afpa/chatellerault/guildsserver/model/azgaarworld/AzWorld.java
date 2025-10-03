package org.afpa.chatellerault.guildsserver.model.azgaarworld;

import java.util.ArrayList;
import java.util.HashMap;

public record AzWorld(
        HashMap<Integer, AzPackCell> cells,
        HashMap<Integer, AzBiome> biomes,
        HashMap<Integer, AzBurg> burgs
) {
}

