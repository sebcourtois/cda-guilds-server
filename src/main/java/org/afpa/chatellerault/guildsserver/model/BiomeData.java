package org.afpa.chatellerault.guildsserver.model;

import lombok.Builder;
import lombok.Data;
import org.afpa.chatellerault.guildsserver.core.TableConfig;
import org.afpa.chatellerault.guildsserver.core.TableConfigField;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class BiomeData {
    UUID id;
    String name;

    public static final class BiomeTable extends TableConfig<BiomeData> {
        public static final String
                name = "biome";
        public static final List<TableConfigField<BiomeData, ?>>
                fields = List.of(
                TableConfigField.builder(
                        "id", UUID.class,
                        BiomeData::getId,
                        BiomeData::setId
                ).isPrimaryKey(true).isGenerated(true).build(),
                TableConfigField.builder(
                        "name", String.class,
                        BiomeData::getName,
                        BiomeData::setName
                ).build()
        );

        public BiomeTable() {
            super(name, fields);
        }
    }
}
