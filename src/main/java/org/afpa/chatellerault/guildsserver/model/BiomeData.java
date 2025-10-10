package org.afpa.chatellerault.guildsserver.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.afpa.chatellerault.guildsserver.util.TableFieldSpec;
import org.afpa.chatellerault.guildsserver.util.TableMappedData;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class BiomeData extends TableMappedData {
    UUID id;
    String name;

    @Override
    public String tableName() {
        return "biome";
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
                ).build()
        );
    }
}
