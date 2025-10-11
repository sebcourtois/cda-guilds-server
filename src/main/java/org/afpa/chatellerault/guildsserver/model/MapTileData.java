package org.afpa.chatellerault.guildsserver.model;

import lombok.Builder;
import lombok.Data;
import org.afpa.chatellerault.guildsserver.core.TableConfig;
import org.afpa.chatellerault.guildsserver.core.TableConfigField;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class MapTileData {
    UUID id;

    @lombok.Builder.Default
    long posX = 0;
    @lombok.Builder.Default
    long posY = 0;

    UUID biomeId;

    public static final class MapTileTable extends TableConfig<MapTileData> {
        public static final String
                name = "map_tile";
        public static final List<TableConfigField<MapTileData, ?>>
                fields = List.of(
                TableConfigField.builder(
                        "id", UUID.class,
                        MapTileData::getId,
                        MapTileData::setId
                ).isPrimaryKey(true).isGenerated(true).build(),
                TableConfigField.builder(
                        "x", Long.class,
                        MapTileData::getPosX,
                        MapTileData::setPosX
                ).build(),
                TableConfigField.builder(
                        "y", Long.class,
                        MapTileData::getPosY,
                        MapTileData::setPosY
                ).build(),
                TableConfigField.builder(
                        "id_biome", UUID.class,
                        MapTileData::getBiomeId,
                        MapTileData::setBiomeId
                ).build()
        );

        public MapTileTable() {
            super(name, fields);
        }
    }

}
