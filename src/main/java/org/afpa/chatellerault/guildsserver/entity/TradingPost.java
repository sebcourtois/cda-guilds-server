package org.afpa.chatellerault.guildsserver.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.afpa.chatellerault.guildsserver.util.TableFieldSpec;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class TradingPost extends BaseEntity {
    UUID id;
    String name;

    @lombok.Builder.Default
    int posX = 0;
    @lombok.Builder.Default
    int posY = 0;
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
                TableFieldSpec.builder("id").
                        getter(this::getId)
                        .isPrimaryKey(true)
                        .isGenerated(true)
                        .build(),
                TableFieldSpec.builder("name")
                        .getter(this::getName)
                        .build(),
                TableFieldSpec.builder("location_x")
                        .getter(this::getPosX)
                        .build(),
                TableFieldSpec.builder("location_y")
                        .getter(this::getPosY)
                        .build(),
                TableFieldSpec.builder("population")
                        .getter(this::getPopulation)
                        .build(),
                TableFieldSpec.builder("id_host")
                        .getter(this::getHostId)
                        .build()
        );
    }
}
