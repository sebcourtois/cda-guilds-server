package org.afpa.chatellerault.guildsserver.azgaarworld;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;


public class AzWorld {
    private static final Logger LOG = LogManager.getLogger(AzWorld.class);

    private final ArrayList<AzPackCell> cells;
    private final ArrayList<AzBiome> biomes;
    private final ArrayList<AzBurg> burgs;

    public AzWorld(
            ArrayList<AzPackCell> cells,
            ArrayList<AzBiome> biomes,
            ArrayList<AzBurg> burgs
    ) {
        this.cells = cells;
        this.biomes = biomes;
        this.burgs = burgs;
    }

    public static AzWorld fromJson(InputStream jsonStream) throws IOException {
        JsonNode rootNode = new com.fasterxml.jackson.databind.ObjectMapper().readTree(jsonStream);
        ArrayList<AzBurg> burgs = AzWorld.readBurgsFromJsonTree(rootNode);
        ArrayList<AzBiome> biomes = AzWorld.readBiomesFromJsonTree(rootNode);
        ArrayList<AzPackCell> cells = AzWorld.readCellsFromJsonTree(rootNode);
        return new AzWorld(cells, biomes, burgs);
    }

    private static ArrayList<AzPackCell> readCellsFromJsonTree(JsonNode rootNode) {
        JsonNode cellsNode = rootNode.get("pack").get("cells");
        int numCells = cellsNode.size();
        LOG.info("{} cells", numCells);
        var cells = new ArrayList<AzPackCell>(numCells);

        for (var cellNode : cellsNode) {
            int cellId = cellNode.get("i").asInt();
            double[] position = cellNode.get("p").valueStream().mapToDouble(JsonNode::asDouble).toArray();
            int biomeId = cellNode.get("biome").asInt();
            int burgId = cellNode.get("burg").asInt();
            cells.add(new AzPackCell(
                    cellId,
                    position,
                    biomeId,
                    burgId
            ));
        }
        return cells;
    }

    private static ArrayList<AzBiome> readBiomesFromJsonTree(JsonNode rootNode) {
        JsonNode biomesRootNode = rootNode.get("biomesData");
        int numBiomes = biomesRootNode.get("i").size();
        var biomes = new ArrayList<AzBiome>(numBiomes);
        JsonNode biomeNamesNode = biomesRootNode.get("name");

        for (int i = 0; i < numBiomes; i++) {
            int biomeId = biomesRootNode.get("i").get(i).asInt();
            String biomeName = biomeNamesNode.get(i).asText();
            biomes.add(new AzBiome(biomeId, biomeName));
        }
        return biomes;
    }

    private static ArrayList<AzBurg> readBurgsFromJsonTree(JsonNode rootNode) {
        JsonNode burgsNode = rootNode.get("pack").get("burgs");
        int numBurgs = burgsNode.size() - 1; // first one always empty
        var burgs = new ArrayList<AzBurg>(numBurgs);
        Iterator<JsonNode> burgsNodeValues = burgsNode.values();
        burgsNodeValues.next(); // skip first one, always empty
        while (burgsNodeValues.hasNext()) {
            JsonNode burgNode = burgsNodeValues.next();
            String burgName = burgNode.get("name").asText();
            int burgId = burgNode.get("i").asInt();
            var newBurg = new AzBurg(
                    burgId, burgName,
                    burgNode.get("x").asDouble(),
                    burgNode.get("y").asDouble(),
                    burgNode.get("population").asDouble(),
                    burgNode.get("cell").asInt()
            );
            burgs.add(newBurg);
        }
        return burgs;
    }

    public ArrayList<AzPackCell> cells() {
        return this.cells;
    }

    public ArrayList<AzBiome> biomes() {
        return this.biomes;
    }

    public ArrayList<AzBurg> burgs() {
        return this.burgs;
    }
}

