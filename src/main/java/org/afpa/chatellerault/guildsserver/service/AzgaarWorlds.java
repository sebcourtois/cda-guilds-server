package org.afpa.chatellerault.guildsserver.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.afpa.chatellerault.guildsserver.model.azgaarworld.AzBiome;
import org.afpa.chatellerault.guildsserver.model.azgaarworld.AzBurg;
import org.afpa.chatellerault.guildsserver.model.azgaarworld.AzPackCell;
import org.afpa.chatellerault.guildsserver.model.azgaarworld.AzWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class AzgaarWorlds {
    private static final Logger LOG = LogManager.getLogger(AzgaarWorlds.class);

    public static AzWorld loadFromJson(InputStream inputStream) throws IOException {
        JsonNode rootNode = new ObjectMapper().readTree(inputStream);
        HashMap<Integer, AzBurg> burgs = AzgaarWorlds.readBurgsFromJsonTree(rootNode);
        HashMap<Integer, AzBiome> biomes = AzgaarWorlds.readBiomesFromJsonTree(rootNode);
        HashMap<Integer, AzPackCell> cells = AzgaarWorlds.readCellsFromJsonTree(rootNode);
        return new AzWorld(cells, biomes, burgs);
    }

    private static HashMap<Integer, AzPackCell> readCellsFromJsonTree(JsonNode rootNode) {
        JsonNode cellsNode = rootNode.get("pack").get("cells");
        int numCells = cellsNode.size();
        LOG.info("{} cells", numCells);
        var cells = new HashMap<Integer, AzPackCell>(numCells);

        for (var cellNode : cellsNode) {
            double[] position = cellNode.get("p").valueStream().mapToDouble(JsonNode::asDouble).toArray();
            int biomeIdx = cellNode.get("biome").asInt();
            int burgIdx = cellNode.get("burg").asInt() - 1;
            cells.put(
                    cellNode.get("i").asInt(),
                    new AzPackCell(position, biomeIdx, burgIdx)
            );
        }
        return cells;
    }

    private static HashMap<Integer, AzBiome> readBiomesFromJsonTree(JsonNode rootNode) {
        JsonNode biomesRootNode = rootNode.get("biomesData");
        int numBiomes = biomesRootNode.get("i").size();
        var biomes = new HashMap<Integer, AzBiome>(numBiomes);
        JsonNode biomeNamesNode = biomesRootNode.get("name");

        for (int i = 0; i < numBiomes; i++) {
            biomes.put(
                    biomesRootNode.get("i").get(i).asInt(),
                    new AzBiome(biomeNamesNode.get(i).asText())
            );
        }
        return biomes;
    }

    private static HashMap<Integer, AzBurg> readBurgsFromJsonTree(JsonNode rootNode) {
        JsonNode burgsNode = rootNode.get("pack").get("burgs");
        int numBurgs = burgsNode.size() - 1; // first one always empty
        var burgs = new HashMap<Integer, AzBurg>(numBurgs);
        var burgNames = new HashSet<String>();

        Iterator<JsonNode> burgsNodeValues = burgsNode.values();
        burgsNodeValues.next(); // skip first one, always empty
        while (burgsNodeValues.hasNext()) {
            JsonNode burgNode = burgsNodeValues.next();
            String burgName = burgNode.get("name").asText();
            if (!burgNames.add(burgName)) continue; // skip burg if name already used
            var newBurg = new AzBurg(
                    burgName,
                    burgNode.get("x").asDouble(),
                    burgNode.get("y").asDouble(),
                    burgNode.get("population").asDouble(),
                    burgNode.get("cell").asInt()
            );
            burgs.put(burgNode.get("i").asInt(), newBurg);
        }
        return burgs;
    }

}
