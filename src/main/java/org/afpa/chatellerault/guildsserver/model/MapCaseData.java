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
public class MapCaseData extends TableRowData {
    UUID id;

    @lombok.Builder.Default
    long posX = 0;
    @lombok.Builder.Default
    long posY = 0;

    UUID biomeId;

    @Override
    public String tableName() {
        return "map_case";
    }

    @Override
    public List<TableFieldSpec> tableFields() {
        return List.of(
                TableFieldSpec.builder(
                        "id",
                        UUID.class, this::getId, val -> this.setId((UUID) val)
                ).isPrimaryKey(true).isGenerated(true).build(),
                TableFieldSpec.builder(
                        "x",
                        Long.class, this::getPosX, val -> this.setPosX((long) val)
                ).build(),
                TableFieldSpec.builder(
                        "y",
                        Long.class, this::getPosY, val -> this.setPosY((long) val)
                ).build(),
                TableFieldSpec.builder(
                        "id_biome",
                        UUID.class, this::getBiomeId, val -> this.setBiomeId((UUID) val)
                ).build()
        );
    }
}
