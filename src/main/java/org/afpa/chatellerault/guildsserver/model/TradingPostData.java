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
public class TradingPostData extends TableMappedData {
    UUID id;
    String name;

    @lombok.Builder.Default
    long posX = 0;
    @lombok.Builder.Default
    long posY = 0;
    @lombok.Builder.Default
    int population = 0;

    UUID hostId;

    @Override
    public String tableName() {
        return "trading_post";
    }

    @Override
    public List<TableFieldSpec> tableFields() {
        return List.of(
                TableFieldSpec.builder("id",
                        UUID.class, this::getId, val -> this.setId((UUID) val)
                ).isPrimaryKey(true).isGenerated(true).build(),
                TableFieldSpec.builder("name",
                        String.class, this::getName, val -> this.setName((String) val)
                ).build(),
                TableFieldSpec.builder("location_x",
                        Long.class, this::getPosX, val -> this.setPosX((long) val)
                ).build(),
                TableFieldSpec.builder("location_y",
                        Long.class, this::getPosY, val -> this.setPosY((long) val)
                ).build(),
                TableFieldSpec.builder("population",
                        Integer.class, this::getPopulation, val -> this.setPopulation((int) val)
                ).build(),
                TableFieldSpec.builder("id_host",
                        UUID.class, this::getHostId, val -> this.setHostId((UUID) val)
                ).build()
        );
    }
}
