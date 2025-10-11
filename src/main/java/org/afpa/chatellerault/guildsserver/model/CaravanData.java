package org.afpa.chatellerault.guildsserver.model;

import lombok.Builder;
import lombok.Data;
import org.afpa.chatellerault.guildsserver.core.TableConfig;
import org.afpa.chatellerault.guildsserver.core.TableConfigField;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CaravanData {
    UUID id;
    String name;
    UUID destinationId;
    UUID mapTileId;

    public static final class CaravanTable extends TableConfig<CaravanData> {
        public static final String
                name = "caravan";
        public static final List<TableConfigField<CaravanData, ?>>
                fields = List.of(
                TableConfigField.builder(
                        "id", UUID.class,
                        CaravanData::getId,
                        CaravanData::setId
                ).isPrimaryKey(true).isGenerated(true).build(),
                TableConfigField.builder(
                        "name", String.class,
                        CaravanData::getName,
                        CaravanData::setName
                ).build(),
                TableConfigField.builder(
                        "id_destination", UUID.class,
                        CaravanData::getDestinationId,
                        CaravanData::setDestinationId
                ).build(),
                TableConfigField.builder(
                        "location", UUID.class,
                        CaravanData::getMapTileId,
                        CaravanData::setMapTileId
                ).build()
        );

        public CaravanTable() {
            super(name, fields);
        }
    }
}
