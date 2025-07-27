package org.afpa.chatellerault.guildsserver.entity;

import lombok.*;
import org.afpa.chatellerault.guildsserver.util.TableFieldSpec;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class Caravan extends BaseEntity {
    UUID id;
    String name;

    @Builder.Default
    long posX = 0;
    @Builder.Default
    long posY = 0;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    UUID destinationId;
    TradingPost destination;

    @Override
    public String tableName() {
        return "caravan";
    }

    @Override
    public List<TableFieldSpec> tableFields() {
        return List.of(
                TableFieldSpec.builder("id",
                                UUID.class, this::getId, val -> this.setId((UUID) val)
                        ).isPrimaryKey(true)
                        .isGenerated(true)
                        .build(),
                TableFieldSpec.builder("name",
                        String.class, this::getName, val -> this.setName((String) val)
                ).build(),
                TableFieldSpec.builder("location_x",
                        Long.class, this::getPosX, val -> this.setPosX((long) val)
                ).build(),
                TableFieldSpec.builder("location_y",
                        Long.class, this::getPosY, val -> this.setPosY((long) val)
                ).build(),
                TableFieldSpec.builder("id_destination",
                        UUID.class, this::getDestinationId, val -> this.setDestinationId((UUID) val)
                ).build()
        );
    }

    private @Nullable UUID getDestinationId() {
        return (this.destination != null) ? this.destination.getId() : this.destinationId;
    }

    public void setDestinationId(UUID destinationId) {
        this.destinationId = destinationId;
        if (this.destination != null && !this.destination.getId().equals(this.destinationId)) {
            this.destination = null;
        }

    }
}
