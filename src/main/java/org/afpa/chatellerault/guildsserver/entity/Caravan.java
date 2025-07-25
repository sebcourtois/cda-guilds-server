package org.afpa.chatellerault.guildsserver.entity;

import lombok.EqualsAndHashCode;
import org.afpa.chatellerault.guildsserver.util.TableFieldSpec;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@lombok.Data
@lombok.Builder
public class Caravan extends BaseEntity {
    UUID id;
    String name;

    @lombok.Builder.Default
    int posX = 0;
    @lombok.Builder.Default
    int posY = 0;

    TradingPost destination;

    @Override
    public String tableName() {
        return "caravan";
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
                TableFieldSpec.builder("destination")
                        .getter(this::getDestinationId)
                        .build()
        );
    }

    private @Nullable UUID getDestinationId() {
        return (this.destination != null) ? this.destination.getId() : null;
    }
}
