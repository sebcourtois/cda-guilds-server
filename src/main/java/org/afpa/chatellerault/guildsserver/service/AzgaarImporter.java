package org.afpa.chatellerault.guildsserver.service;

import org.afpa.chatellerault.guildsserver.azgaarworld.AzBiome;
import org.afpa.chatellerault.guildsserver.azgaarworld.AzBurg;
import org.afpa.chatellerault.guildsserver.azgaarworld.AzPackCell;
import org.afpa.chatellerault.guildsserver.azgaarworld.AzWorld;
import org.afpa.chatellerault.guildsserver.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

@Service
public class AzgaarImporter {
    private static final Logger LOG = LogManager.getLogger(AzgaarImporter.class);

    public static void importWorld(AzWorld azWorld) throws SQLException {
        HashMap<Integer, Biome> importedBiomes = AzgaarImporter.importBiomes(azWorld.biomes());
        LOG.info("Imported biomes: {}/{}", importedBiomes.size(), azWorld.biomes().size());
        HashMap<Integer, MapTile> importedMapTiles = AzgaarImporter.importCells(azWorld.cells(), importedBiomes);
        LOG.info("Imported map tiles: {}/{}", importedMapTiles.size(), azWorld.cells().size());
        HashMap<Integer, TradingPost> importedTradingPosts = AzgaarImporter.importBurgs(azWorld.burgs(), importedMapTiles);
        LOG.info("Imported trading posts: {}/{}", importedTradingPosts.size(), azWorld.burgs().size());
    }

    private static HashMap<Integer, Biome> importBiomes(ArrayList<AzBiome> azgaarBiomes) throws SQLException {
        var importedBiomes = new HashMap<Integer, Biome>(azgaarBiomes.size());
        for (AzBiome azBiome : azgaarBiomes) {
            Biome biome = Biomes.create(BiomeData.builder()
                    .name(azBiome.name())
                    .build()
            );
            importedBiomes.put(azBiome.id(), biome);
        }
        return importedBiomes;
    }

    private static HashMap<Integer, MapTile> importCells(
            ArrayList<AzPackCell> azgaarCells,
            HashMap<Integer, Biome> importedBiomes
    ) throws SQLException {
        var importedMapTiles = new HashMap<Integer, MapTile>(azgaarCells.size());
        for (AzPackCell azCell : azgaarCells) {
            MapTile mapTile = MapTiles.create(MapTileData.builder()
                    .posX((long) azCell.position()[0])
                    .posY((long) azCell.position()[1])
                    .biomeId(importedBiomes.get(azCell.biomeId()).getId())
                    .build()
            );
            importedMapTiles.put(azCell.id(), mapTile);
        }
        return importedMapTiles;
    }

    private static HashMap<Integer, TradingPost> importBurgs(
            ArrayList<AzBurg> azgaarBurgs,
            HashMap<Integer, MapTile> importedMapTiles
    ) throws SQLException {
        var importedTradingPosts = new HashMap<Integer, TradingPost>(azgaarBurgs.size());
        var burgNames = new HashSet<String>();
        for (AzBurg azBurg : azgaarBurgs) {
            String burgName = azBurg.name();
            if (!burgNames.add(burgName)) {
                LOG.info("Trading post's name must be unique. Ignoring {}", azBurg);
                continue;
            }
            TradingPost tradingPost = TradingPosts.create(TradingPostData.builder()
                    .name(burgName)
                    .population((int) azBurg.population() * 1000)
                    .mapTileId(importedMapTiles.get(azBurg.cellId()).getId())
                    .build()
            );
            importedTradingPosts.put(azBurg.id(), tradingPost);
        }
        return importedTradingPosts;
    }
}
