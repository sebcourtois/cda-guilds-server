package org.afpa.chatellerault.guildsserver.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.afpa.chatellerault.guildsserver.util.TableFieldSpec;
import org.afpa.chatellerault.guildsserver.util.TableRowData;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class CaravanData extends TableRowData {
    UUID id;
    String name;
    UUID destinationId;
    UUID mapTileId;

    @Override
    public String tableName() {
        return "caravan";
    }

    @Override
    public List<TableFieldSpec> tableFields() {
        return List.of(
                TableFieldSpec.builder(
                        "id",
                        UUID.class, this::getId, val -> this.setId((UUID) val)
                ).isPrimaryKey(true).isGenerated(true).build(),
                TableFieldSpec.builder(
                        "name",
                        String.class, this::getName, val -> this.setName((String) val)
                ).build(),
                TableFieldSpec.builder(
                        "id_destination",
                        UUID.class, this::getDestinationId, val -> this.setDestinationId((UUID) val)
                ).build(),
                TableFieldSpec.builder(
                        "location",
                        UUID.class, this::getMapTileId, val -> this.setMapTileId((UUID) val)
                ).build()
        );
    }
}
